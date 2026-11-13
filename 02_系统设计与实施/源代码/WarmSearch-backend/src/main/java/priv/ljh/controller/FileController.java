package priv.ljh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import priv.ljh.common.Result;
import priv.ljh.service.FileService;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "文件上传")
@RestController
public class FileController {

    @Resource
    private FileService fileService;

    @ApiOperation("上传图片/文件，返回可访问URL")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.upload(file));
    }
}
