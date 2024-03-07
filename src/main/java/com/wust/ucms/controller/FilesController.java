package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.Files;
import com.wust.ucms.service.impl.FilesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FilesController {

    @Autowired
    FilesServiceImpl file;

    @PostMapping("/create")
    public Result createFiles(
            @RequestParam MultipartFile multipartFile,
            @RequestParam Files files,
            @RequestParam String md5Code) {
        return null;
    }
}
