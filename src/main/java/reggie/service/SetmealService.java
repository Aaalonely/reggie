package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.dto.SetmealDto;
import reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    public void saveAndDish(SetmealDto setmealDto);
}
