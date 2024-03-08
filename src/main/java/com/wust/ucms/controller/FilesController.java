package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.*;
import com.wust.ucms.service.impl.FilesServiceImpl;
import com.wust.ucms.utils.FileUtil;
import com.wust.ucms.utils.MD5Util;
import com.wust.ucms.utils.ZIPUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipOutputStream;

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
        try {
            if (multipartFile == null ||
                    files.getFileName() == null || files.getFileName().isEmpty() ||
                    md5Code == null || md5Code.isEmpty()
            ) throw new Exception("缺少请求参数！");
        } catch (Exception e) {
            return new Result(-20000);
        }

        try {
            if (files.getFileName().length() > 22) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return new Result(-20002);
        }

        try {
            int count = 0;
            if (files.getLoginId() != null && files.getLoginId() != 0) count++;
            if (files.getClubId() != null && files.getClubId() != 0) count++;
            if (files.getMemberId() != null && files.getMemberId() != 0) count++;
            if (files.getActivityId() != null && files.getActivityId() != 0) count++;
            if (files.getFundId() != null && files.getFundId() != 0) count++;
            if (count != 1) throw new Exception("参数逻辑错误！");
        } catch (Exception e) {
            return new Result(-20006);
        }

        String md5 = MD5Util.calcMD5(multipartFile);
        if (!md5.equals(md5Code)) return new Result(-20500);
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) return new Result(-20501);
        String extName = originalFileName.substring(originalFileName.lastIndexOf("."));
        String suffixList = ".pdf, .doc, .docx, .xls, .xlsx, .jpg, .png";
        if (!suffixList.contains(extName.trim().toLowerCase())) return new Result(-20502);

        String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String now = String.valueOf(Calendar.getInstance().getTimeInMillis());

        File location = FileUtil.getURL();
        String fileName = name + now + extName;
        files.setFileName(fileName);
        File targetFile = new File(location, fileName);

        Integer flag = file.createFiles(files);
        if (flag == 0) {
            try {
                file.saveFiles(multipartFile.getInputStream(), targetFile);
                Map<String, Object> data = new HashMap<>();
                data.put("fileName", fileName);
                return new Result(0, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Result(flag);
    }

    @PostMapping("/delete")
    public Result deleteFiles(@RequestBody Files files) {
        try {
            if (files.getFileName() == null || files.getFileName().isEmpty()) throw new Exception("缺少参数！");
        } catch (Exception e) {
            return new Result(-20001);
        }

        Integer flag = file.deleteFiles(files.getFileName());

        if (flag == 0) {
            flag = file.removeFiles(files.getFileName());
        }

        return new Result(flag);
    }

    @PostMapping("/research/login")
    public Result researchLoginFiles(@RequestBody LoginInfo loginInfo) {
        if (loginInfo.getEmail() == null || loginInfo.getEmail().isEmpty()) return new Result(-20001);
        List<String> filesList = file.researchFileNameByLoginId(loginInfo.getEmail());
        if (filesList == null) return new Result(-20003);
        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @PostMapping("/research/club")
    public Result researchClubFiles(@RequestBody ClubInfo clubInfo) {
        if (clubInfo.getId() == null) return new Result(-20001);
        List<String> filesList = file.researchFileNameByClubId(clubInfo.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @PostMapping("/research/member")
    public Result researchMemberFiles(@RequestBody MemberInfo memberInfo) {
        if (memberInfo.getStudentNumber() == null || memberInfo.getStudentNumber().isEmpty())
            return new Result(-20001);
        List<String> filesList = file.researchFileNameByMemberId(memberInfo.getStudentNumber());
        if (filesList == null) return new Result(-20003);
        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @PostMapping("/research/activity")
    public Result researchActivityFiles(@RequestBody ActivityInfo activityInfo) {
        if (activityInfo.getId() == null) return new Result(-20001);
        List<String> filesList = file.researchFileNameByActivityId(activityInfo.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @PostMapping("/research/fund")
    public Result researchFundFiles(@RequestBody FundInfo fundInfo) {
        if (fundInfo.getId() == null) return new Result(-20001);
        List<String> filesList = file.researchFileNameByFundId(fundInfo.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @GetMapping("/download")
    public Result downloadFile(HttpServletResponse response, String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) return new Result(-20000);

        File location = FileUtil.getURL();
        String targetFile = new File(location, fileName).getPath();
        String fileType = targetFile.substring(targetFile.lastIndexOf(".")+1);

        response.reset();
        if ("jpg,png".contains(fileType)) {
            response.setContentType("image/"+fileType);
        } else if (fileType.equals("doc")) {
            response.setContentType("application/msword");
        } else if (fileType.equals("docx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else if (fileType.equals("xls")) {
            response.setContentType("application/vnd.ms-excel");
        } else if (fileType.equals("xlsx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else if (fileType.equals("pdf")) {
            response.setContentType("application/pdf");
        } else {
            response.setContentType("multipart/form-data");
        }

        response.setHeader("Content-Disposition",
                "attachment;filename=" + Arrays.toString(targetFile.getBytes(StandardCharsets.UTF_8)));
        response.setHeader("Access-Control-Allow-Origin", "*");
        file.downloadFiles(response.getOutputStream(), targetFile);

        return new Result(0);
    }

    @GetMapping("/zip/fund")
    public void downloadFundZIP(HttpServletResponse response, Integer fundId, String theme) throws IOException {
        String name = "经费申请材料-" + theme;
        String fileName = URLEncoder.encode(name+".zip", StandardCharsets.UTF_8);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment;filename="+fileName);
        response.flushBuffer();

        List<String> filesList = file.researchFileNameByFundId(fundId);
        ZIPUtil.compressFiles(new ZipOutputStream(response.getOutputStream()), filesList);
    }
}
