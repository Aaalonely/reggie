package reggie.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reggie.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class UploadController {
    @Value("${reggie.path}")
    private String basepath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString()+substring;
        File dir = new File(basepath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.transferTo(new File(basepath + filename));
        return R.success(filename);
    }
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(basepath + name));
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        byte[] buffer = new byte[1024];
        int len=0;
        while ((len = fileInputStream.read(buffer)) > -1) {
            outputStream.write(buffer, 0, len);
        }
        fileInputStream.close();
        outputStream.close();
    }
}
