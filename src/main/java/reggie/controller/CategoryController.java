package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.Category;
import reggie.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }
    @DeleteMapping
    public R<String> delete(Long ids) {
        return categoryService.remove(ids);
    }
    @PostMapping
    public R<String> save(@RequestBody Category category){
//        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Category::getName, category.getName()).or().eq(Category::getSort,category.getSort());
//        if (categoryService.count(queryWrapper) == 0) {
            categoryService.save(category);
            return R.success("添加成功");
//        } else {
//            return R.error("添加失败,名称或排序已存在");
//        }
    }
    @PutMapping
    public R<String> update(@RequestBody Category category) {
//        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.ne(Category::getId,category.getId()).and(i->i.eq(Category::getName, category.getName()).or().eq(Category::getSort,category.getSort()));
//        if (categoryService.count(queryWrapper) == 0) {
            categoryService.updateById(category);
            return R.success("修改成功");
//        } else {
//            return R.error("修改失败,名称或排序已存在");
//        }
    }
    @GetMapping("/list")
    public R<List<Category>> list(Integer type){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type!=null,Category::getType, type);
        return R.success(categoryService.list(queryWrapper));
    }
}
