package com.mqq.controller.common;

import com.mqq.result.Result;
import com.mqq.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class UploadController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传");
        String OriginalFileName = file.getOriginalFilename();

        String extension = OriginalFileName.substring(OriginalFileName.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + extension;

        byte[] content = file.getBytes();

        String url = null;
        if(!OriginalFileName.isBlank()){
            url = aliOssUtil.upload(content,objectName);
        }
        return Result.success(url);
    }
}
