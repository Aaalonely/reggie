package reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reggie.common.R;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class ExpcetionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExpcetionHandler(SQLIntegrityConstraintViolationException ex) {
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] s = ex.getMessage().split(" ");
            return R.error(s[2] + "已存在");
        }
        log.error(ex.getMessage());
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> ExpcetionHandler(CustomException ex) {
        return R.error(ex.getMessage());
    }
}
