package reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.dto.DishDto;
import reggie.mapper.DishDtoMapper;
import reggie.service.DishDtoService;

@Service
public class DishDtoServiceImpl extends ServiceImpl<DishDtoMapper, DishDto>implements DishDtoService {
}
