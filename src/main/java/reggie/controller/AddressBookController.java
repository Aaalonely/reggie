package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.AddressBook;
import reggie.service.AddressBookService;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getThreadLocal());
        List<AddressBook> addressBooks = addressBookService.list(lambdaQueryWrapper);
        return R.success(addressBooks);
    }

    @PostMapping
    public R<AddressBook> add(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getThreadLocal());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable("id") Long id){
        return R.success(addressBookService.getById(id));
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("更改成功");
    }
    @PutMapping("/default")
    public R<AddressBook> updateDefault(@RequestBody AddressBook addressBook ) {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getThreadLocal());
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);
        for (AddressBook address:list
             ) {
            address.setIsDefault(0);
        }
        addressBookService.updateBatchById(list);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
    @GetMapping("/default")
    public R<AddressBook> getAddress(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getThreadLocal());
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        return R.success(addressBookService.getOne(lambdaQueryWrapper));
    }
    @DeleteMapping
    public R<String> delete(Long ids) {
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }
}
