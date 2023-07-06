package reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.entity.DishFlavor;
import reggie.mapper.DishflavorMapper;
import reggie.service.DishflavorService;

@Service
public class DishflavorServiceImpl extends ServiceImpl<DishflavorMapper, DishFlavor> implements DishflavorService {
}
