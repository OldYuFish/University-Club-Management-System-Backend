package com.wust.ucms.service.impl;

import com.wust.ucms.mapper.FilesMapper;
import com.wust.ucms.pojo.Files;
import com.wust.ucms.service.FilesService;
import com.wust.ucms.utils.FileUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.util.List;

@Service
public class FilesServiceImpl implements FilesService {

    @Value("${file.host}")
    private String fileHost;

    @Autowired
    FilesMapper file;

    @Override
    public Integer createFiles(Files files) {
        String fileName = files.getFileName();
        files.setSrc(fileHost + fileName);
        int flag = file.insert(files);

        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public void saveFiles(InputStream file, File filePath) {
        try {
            BufferedInputStream in = new BufferedInputStream(file);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer deleteFiles(String fileName) {
        int flag = file.deleteFileByFileName(fileName);
        if (flag > 0) return 0;

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        return -20003;
    }

    @Override
    public Integer removeFiles(String fileName) {
        File location = FileUtil.getURL();
        File targetFile = new File(location, fileName);
        if (targetFile.delete()) return 0;

        return -20104;
    }

    @Override
    public void downloadFiles(OutputStream os, String filePath) throws IOException {
        File targetFile = new File(filePath);
        InputStream is = new FileInputStream(targetFile);
        IOUtils.copy(is, os);
        os.flush();
        is.close();
        os.close();
    }

    @Override
    public List<String> researchFileNameByLoginId(Integer loginId) {
        return file.selectFileNameByLoginId(loginId);
    }

    @Override
    public List<String> researchFileNameByClubId(Integer clubId) {
        return file.selectFileNameByClubId(clubId);
    }

    @Override
    public List<String> researchFileNameByMemberId(Integer memberId) {
        return file.selectFileNameByMemberId(memberId);
    }

    @Override
    public List<String> researchFileNameByActivityId(Integer activityId) {
        return file.selectFileNameByActivityId(activityId);
    }

    @Override
    public List<String> researchFileNameByFundId(Integer fundId) {
        return file.selectFileNameByFundId(fundId);
    }

    @Override
    public List<String> researchFileNameByCompetitionId(Integer competitionId) {
        return file.selectFileNameByCompetitionId(competitionId);
    }
}
