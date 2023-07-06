package reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggie.dto.DishDto;

@Mapper
public interface DishDtoMapper extends BaseMapper<DishDto> {
}
