package com.meeleet.cloud.common.file.controller;

import com.meeleet.cloud.common.file.service.MinioService;
import com.meeleet.cloud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final MinioService minioService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "文件上传")
    @SneakyThrows
    public Result<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储桶名称(没值默认存储桶)") @RequestParam(value = "bucketName", required = false) String bucketName

    ) {
        String path = minioService.putObject(file, bucketName);
        return Result.success(path);
    }

    @DeleteMapping(value = "/delete")
    @Operation(description = "文件删除")
    @SneakyThrows
    public Result deleteFile(
            @Parameter(description = "文件路径") @RequestParam String path) {
        int lastIndex = path.lastIndexOf("/");
        String bucket = path.substring(path.lastIndexOf("/", lastIndex - 1) + 1, lastIndex);
        String fileName = path.substring(lastIndex + 1);
        minioService.removeObject(bucket, fileName);
        return Result.success();
    }

}
