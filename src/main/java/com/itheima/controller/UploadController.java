package com.itheima.controller;


import com.itheima.common.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/common")
public class UploadController {


    //上传菜品图片->的路径：(application.yml)
    @Value("${reggie-files.path}")
    private String filePath;


    //图片上传功能
    @PostMapping("/upload")
    public R<String> uploadPicture(MultipartFile file) throws IOException { //参数名称要和浏览器请求里的保持一致

        //上传的文件只是临时文件，会消失，所以要做一个转存：transfer to
        //获取原始文件名称
        String originalFilename = file.getOriginalFilename(); // abc.jpg
        //截取后缀jpg：
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //获取UUID：(生成一个uuid, 防止原始文件名重复造成的文件覆盖）
        String fileName = UUID.randomUUID().toString() + suffix; // bcde.jpg

        //判断指定的文件夹是否存在：
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        //把文件转存到指定位置：
        file.transferTo(new File(filePath + fileName));

        return R.success(fileName);
    }



    //图片下载功能
    @GetMapping("/download")
    public void downloadPictures(String name, HttpServletResponse response) throws IOException {
        //创建一个输入流->获取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(filePath + name));
        //创建一个输出流->回传给浏览器->展示出来
        ServletOutputStream outputStream = response.getOutputStream();

        //以后用到哪学到哪，不用的千万别浪费时间学，全忘了！
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }

        outputStream.close();
        fileInputStream.close();
    }


}
