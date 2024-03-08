package com.wust.ucms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {
    public static void compressFiles(ZipOutputStream out, List<String> filesList) throws IOException {
        File parent = FileUtil.getURL();
        for (String fileName : filesList) {
            File file = new File(parent, fileName);
            FileInputStream input = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(fileName);
            out.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = input.read(bytes)) >= 0) {
                out.write(bytes, 0, len);
            }
            input.close();
        }
        out.close();
    }
}
