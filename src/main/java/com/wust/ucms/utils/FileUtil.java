package com.wust.ucms.utils;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtil {

    public static File getURL() {
        File upload = null;
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if (!path.exists()) path = new File("");
            upload = new File(path.getAbsolutePath(), "files/");
            if (!upload.exists()) upload.mkdirs();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return upload;
    }
}
