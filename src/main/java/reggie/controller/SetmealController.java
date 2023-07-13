package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.dto.SetmealDto;
import reggie.entity.*;
import reggie.service.CategoryService;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService  setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CacheManager cacheManager;

    @Value("${reggie.path}")
    private String basepath;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(setmealDtos);
        return R.success(dtoPage);
    }

    @PostMapping
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public R<String> insert(@RequestBody SetmealDto setmealDto){
        setmealService.saveAndDish(setmealDto);
        return R.success("添加成功");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public R<String> update(Long[] ids,@PathVariable Integer status) {
        List<Long> list = Arrays.asList(ids);
        List<Setmeal> setmeals = setmealService.listByIds(list);
        for (Setmeal setmeal: setmeals) {
            setmeal.setStatus(status);
        }
        setmealService.updateBatchById(setmeals);
        return R.success("修改成功");
    }

    @DeleteMapping
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public R<String> delete(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        List<Setmeal> setmeals = setmealService.listByIds(list);
        for (Setmeal setmeal: setmeals) {
            String image = setmeal.getImage();
            File file = new File(basepath+image);
            if (!file.exists()) {
                return R.error("图片不存在");
            }
            file.delete();
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,list);
        setmealDishService.remove(queryWrapper);
        setmealService.removeByIds(list);
        return R.success("删除成功");
    }
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDto.setSetmealDishes(setmealDishService.list(queryWrapper));
        return R.success(setmealDto);
    }
    @PutMapping
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        if (list != null && list.size() > 0) {
            for (SetmealDish dish: list) {
                dish.setSetmealId(setmealDto.getId());
            }
            setmealDishService.saveBatch(list);
        }

        return R.success("修改成功");
    }
    @Cacheable(value = "SetmealCache" ,key = "#setmeal.categoryId+'_'+#setmeal.status")
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal){
        List<SetmealDto> setmealDtos = null;
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        setmealDtos = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId, item.getId());
            setmealDto.setSetmealDishes(setmealDishService.list(lambdaQueryWrapper));
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtos);
    }
}
