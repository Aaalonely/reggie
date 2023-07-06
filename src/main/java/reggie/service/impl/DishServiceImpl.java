package reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.mapper.DishMapper;
import reggie.service.DishService;
import reggie.service.DishflavorService;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author 26344
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2023-04-16 12:34:24
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService {
    @Autowired
    private DishflavorService dishflavorService;

    @Override
    @Transactional
    public void saveAndflavor(DishDto dishDto) {
        this.save(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(flavor -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());
        dishflavorService.saveBatch(flavors);
    }
}




