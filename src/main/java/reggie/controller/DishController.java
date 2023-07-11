package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.dto.SetmealDto;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.entity.Setmeal;
import reggie.service.CategoryService;
import reggie.service.DishDtoService;
import reggie.service.DishService;
import reggie.service.DishflavorService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishflavorService dishflavorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${reggie.path}")
    private String basepath;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, queryWrapper);
        BeanUtils.copyProperties(dishPage, dtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> DishDtos = records.stream().map((item) -> {
            DishDto DishDto = new DishDto();
            BeanUtils.copyProperties(item, DishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                DishDto.setCategoryName(category.getName());
            }
            return DishDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(DishDtos);
        return R.success(dtoPage);
    }

    @PostMapping("/status/{status}")
    public R<String> update(Long[] ids, @PathVariable Integer status) {
        List<Long> list = Arrays.asList(ids);
        List<Dish> dishes = dishService.listByIds(list);
        for (Dish dish : dishes) {
            dish.setStatus(status);
        }
        dishService.updateBatchById(dishes);
        return R.success("修改成功");
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveAndflavor(dishDto);
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("添加成功");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        List<Dish> dishes = dishService.listByIds(list);
        for (Dish dish : dishes) {
            String image = dish.getImage();
            File file = new File(basepath + image);
            if (!file.exists()) {
                return R.error("图片不存在");
            }
            file.delete();
        }
        dishService.removeByIds(list);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, list);
        dishflavorService.remove(queryWrapper);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> DishDtos = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        DishDtos =(List<DishDto>) redisTemplate.opsForValue().get(key);
        if(DishDtos!=null){
            return R.success(DishDtos);
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        DishDtos = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            dishDto.setFlavors(dishflavorService.list(lambdaQueryWrapper));
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, DishDtos);
        return R.success(DishDtos);
    }

    @GetMapping("/{id}")
    public R<DishDto> display(@PathVariable Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        dishDto.setFlavors(dishflavorService.list(queryWrapper));
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, dishDto.getId());
        dishflavorService.remove(queryWrapper);
        List<DishFlavor> list = dishDto.getFlavors();
        if (list != null && list.size() > 0) {
            for (DishFlavor flavor : list) {
                flavor.setDishId(dishDto.getId());
            }
            dishflavorService.saveBatch(list);
        }
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改成功");
    }
}
