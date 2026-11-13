package priv.ljh.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import priv.ljh.common.BusinessException;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
public class FileService {

    @Value("${warmsearch.upload.path}")
    private String uploadPath;

    @Value("${warmsearch.upload.url-prefix}")
    private String urlPrefix;

    public Map<String, String> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        }
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath, dateDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("创建上传目录失败：" + dir.getAbsolutePath());
        }
        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            throw new BusinessException("文件保存失败：" + e.getMessage());
        }
        Map<String, String> result = new HashMap<>();
        String url = urlPrefix + "/" + dateDir + "/" + fileName;
        result.put("url", url);
        result.put("fileName", original);
        return result;
    }
}
