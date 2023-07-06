package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.common.R;
import reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public R<String> remove(Long id);
}
