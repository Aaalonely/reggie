package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.entity.Dish;

/**
* @author 26344
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2023-04-16 12:34:24
*/
public interface DishService extends IService<Dish> {
    public void saveAndflavor(DishDto dishDto);
}
