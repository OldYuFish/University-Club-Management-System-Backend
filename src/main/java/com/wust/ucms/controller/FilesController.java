package com.wust.ucms.controller;

import com.wust.ucms.controller.utils.Result;
import com.wust.ucms.pojo.*;
import com.wust.ucms.service.impl.FilesServiceImpl;
import com.wust.ucms.utils.FileUtil;
import com.wust.ucms.utils.MD5Util;
import com.wust.ucms.utils.ZIPUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
            @RequestParam String fileName,
            @RequestParam Integer id,
            @RequestParam String type,
            @RequestParam String md5Code) {
        try {
            if (multipartFile == null ||
                    !StringUtils.hasText(fileName) ||
                    !StringUtils.hasText(md5Code) ||
                    !StringUtils.hasText(type) ||
                    id == null
            ) throw new Exception("缺少请求参数！");
        } catch (Exception e) {
            return new Result(-20000);
        }

        try {
            if (fileName.length() > 22) throw new Exception("参数格式错误！");
        } catch (Exception e) {
            return new Result(-20002);
        }

        Files files = new Files();
        try {
            switch (type) {
                case "user" -> files.setLoginId(id);
                case "club" -> files.setClubId(id);
                case "member" -> files.setMemberId(id);
                case "activity" -> files.setActivityId(id);
                case "fund" -> files.setFundId(id);
                default -> throw new Exception("参数逻辑错误！");
            }
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
        String filename = name + now + extName;
        files.setFileName(filename);
        File targetFile = new File(location, filename);

        Integer flag = file.createFiles(files);
        if (flag == 0) {
            try {
                file.saveFiles(multipartFile.getInputStream(), targetFile);
                Map<String, Object> data = new HashMap<>();
                data.put("fileName", filename);
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
            if (!StringUtils.hasText(files.getFileName())) throw new Exception("缺少参数！");
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
        String email = loginInfo.getEmail();
        if (!StringUtils.hasText(email)) return new Result(-20001);
        String fileName = file.researchFileNameByLoginId(email);
        if (!StringUtils.hasText(fileName)) return new Result(-20000);

        Map<String, Object> data = new HashMap<>();
        data.put("fileName", fileName);

        return new Result(0, data);
    }

    @PostMapping("/research/club/image")
    public Result researchClubImage(@RequestBody ClubInfo clubInfo) {
        if (clubInfo.getId() == null) return new Result(-20001);
        List<String> filesList = file.researchFileNameByClubId(clubInfo.getId());

        File location = FileUtil.getURL();
        for (String fileName : filesList) {
            File targetFile = new File(location, fileName);
            String fileType = targetFile.getPath().substring(targetFile.getPath().lastIndexOf(".")+1);
            if (!"jpg, png".contains(fileType)) filesList.remove(fileName);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @PostMapping("/research/club/file")
    public Result researchClubFile(@RequestBody ClubInfo clubInfo) {
        if (clubInfo.getId() == null) return new Result(-20001);
        List<String> filesList = file.researchFileNameByClubId(clubInfo.getId());

        File location = FileUtil.getURL();
        for (String fileName : filesList) {
            File targetFile = new File(location, fileName);
            String fileType = targetFile.getPath().substring(targetFile.getPath().lastIndexOf(".")+1);
            if ("jpg, png".contains(fileType)) filesList.remove(fileName);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("filesList", filesList);

        return new Result(0, data);
    }

    @PostMapping("/research/activity")
    public Result researchActivityFiles(@RequestBody ActivityInfo activityInfo) {
        Integer activityId = activityInfo.getId();
        if (activityId == null) return new Result(-20001);
        List<String> fileNameList = file.researchFileNameByActivityId(activityId);
        if (fileNameList == null) return new Result(-20000);
        Map<String, Object> data = new HashMap<>();
        data.put("filesList", fileNameList);

        return new Result(0, data);
    }

    @PostMapping("/research/member")
    public Result researchMemberFiles(@RequestBody LoginInfo loginInfo) {
        String studentNumber = loginInfo.getStudentNumber();
        if (!StringUtils.hasText(studentNumber)) return new Result(-20001);
        String fileName = file.researchFileNameByMemberId(studentNumber);
        if (!StringUtils.hasText(fileName)) return new Result(-20000);

        Map<String, Object> data = new HashMap<>();
        data.put("fileName", fileName);

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

    @GetMapping("/picture")
    public Result picture(HttpServletResponse response, String fileName) throws IOException {
        if (!StringUtils.hasText(fileName)) return new Result(-20001);

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        File location = FileUtil.getURL();
        File targetFile = new File(location, fileName);
        String fileType = targetFile.getPath().substring(targetFile.getPath().lastIndexOf(".")+1);
        response.setContentType("image/"+fileType);

        ServletOutputStream out = response.getOutputStream();
        BufferedImage bufferedImage = ImageIO.read(targetFile);
        ImageIO.write(bufferedImage, fileType, out);
        try {
            out.flush();
        } finally {
            out.close();
        }

        return new Result(0);
    }

    @GetMapping("/download")
    public Result downloadFile(HttpServletResponse response, String fileName) throws IOException {
        if (!StringUtils.hasText(fileName)) return new Result(-20000);

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
