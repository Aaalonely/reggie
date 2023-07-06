package reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggie.entity.Dish;

/**
* @author 26344
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2023-04-16 12:34:24
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}




