package com.wust.ucms.service;

import com.wust.ucms.pojo.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FilesService {
    Integer createFiles(Files files);
    void saveFiles(InputStream file, File filePath);
    Integer deleteFiles(String fileName);
    Integer removeFiles(String fileName);
    void downloadFiles(OutputStream os, String filePath) throws IOException;
    List<String> researchFileNameByLoginId(String email);
    List<String> researchFileNameByClubId(Integer clubId);
    List<String> researchFileNameByMemberId(String studentNumber);
    List<String> researchFileNameByActivityId(Integer activityId);
    List<String> researchFileNameByFundId(Integer fundId);

}
