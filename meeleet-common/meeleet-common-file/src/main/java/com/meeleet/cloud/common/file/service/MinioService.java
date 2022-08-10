package com.meeleet.cloud.common.file.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.meeleet.cloud.common.util.ImgUtils;
import io.minio.*;
import io.minio.http.Method;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;


@Component
@ConfigurationProperties(prefix = "minio")
@Slf4j
public class MinioService implements InitializingBean {

    /**
     * MinIO的API地址
     */
    @Setter
    private String endpoint;

    /**
     * 用户名
     */
    @Setter
    private String accessKey;

    /**
     * 密钥
     */
    @Setter
    private String secretKey;

    /**
     * 自定义域名(非必须)
     */
    @Setter
    private String customDomain;

    /**
     * 存储桶名称，默认微服务单独一个存储桶
     */
    @Setter
    private String defaultBucket;

    @Value("${minio.img_compression_enabled:false}")
    private boolean imgCompressionEnabled;

    private MinioClient minioClient;

    @Override
    public void afterPropertiesSet() {
        log.info("初始化 MinIO 客户端...");
        Assert.notBlank(endpoint, "MinIO endpoint不能为空");
        Assert.notBlank(accessKey, "MinIO accessKey不能为空");
        Assert.notBlank(secretKey, "MinIO secretKey不能为空");
        this.minioClient = MinioClient.builder()
                //.endpoint(endpoint, 443, true)
                .endpoint(endpoint).credentials(accessKey, secretKey).build();
    }

    /**
     * 创建存储桶(存储桶不存在)
     *
     * @param bucketName
     */
    @SneakyThrows
    public void createBucketIfAbsent(String bucketName) {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        if (!minioClient.bucketExists(bucketExistsArgs)) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();

            minioClient.makeBucket(makeBucketArgs);

            // 设置存储桶访问权限为PUBLIC， 如果不配置，则新建的存储桶默认是PRIVATE，则存储桶文件会拒绝访问 Access Denied
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs
                    .builder().bucket(bucketName)
                    .config(publicBucketPolicy(bucketName).toString())
                    .build();
            minioClient.setBucketPolicy(setBucketPolicyArgs);
        }
    }

    /**
     * 上传文件对象(默认存储桶)
     *
     * @param file MultipartFile文件对象
     * @return
     */
    public String putObject(MultipartFile file) {
        String fileUrl = putObject(file, defaultBucket);
        return fileUrl;
    }

    /**
     * 上传文件对象
     *
     * @param file       MultipartFile文件对象
     * @param bucketName 存储桶名称
     * @return 上传文件路径
     */
    @SneakyThrows
    public String putObject(MultipartFile file, String bucketName) {
        // 存储桶名称为空则使用默认的存储桶
        if (StrUtil.isBlank(bucketName)) {
            bucketName = defaultBucket;
        }
        // 判断存储桶是否存在
        createBucketIfAbsent(bucketName);

        // 获取文件后缀
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        // 文件名
        String uuid = IdUtil.simpleUUID();
        String fileName = DateUtil.format(LocalDateTime.now(), "yyyy/MM/dd") + "/" + uuid + "." + suffix;

        InputStream inputStream;
        // 是否开启压缩
        if (ImgUtils.isImg(fileName) && imgCompressionEnabled) {
            long fileSize = file.getSize();
            log.info("图片({})压缩前大小：{}KB", uuid, fileSize / 1024);
            float compressQuality = ImgUtils.getCompressQuality(fileSize);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Convert.toInt(fileSize));
            Thumbnails.of(file.getInputStream())
                    .scale(1f) // 图片大小比例
                    .outputQuality(compressQuality) // 图片质量压缩比
                    .toOutputStream(outputStream);
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            log.info("图片({})压缩后大小：{}KB", uuid, inputStream.available() / 1024);
        } else {
            inputStream = file.getInputStream();
        }

        // 上传参数构建
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .contentType(file.getContentType())
                .stream(inputStream, inputStream.available(), -1)
                .build();
        // 上传
        minioClient.putObject(putObjectArgs);

        String fileUrl;
        if (StrUtil.isBlank(customDomain)) { // 没有自定义文件路径域名
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName).object(fileName)
                    .method(Method.GET)
                    .build();

            fileUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
            fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
        } else {
            // 自定义文件路径域名，Nginx配置代理转发
            fileUrl = customDomain + '/' + bucketName + "/" + fileName;
        }
        return fileUrl;
    }

    public void removeObject(String bucket, String fileName) throws Exception {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(fileName).build();
        minioClient.removeObject(removeObjectArgs);
    }


    /**
     * PUBLIC桶策略
     * 如果不配置，则新建的存储桶默认是PRIVATE，则存储桶文件会拒绝访问 Access Denied
     *
     * @param bucketName
     * @return
     */
    private static StringBuilder publicBucketPolicy(String bucketName) {
        /**
         * AWS的S3存储桶策略
         * Principal: 生效用户对象
         * Resource:  指定存储桶
         * Action: 操作行为
         */
        StringBuilder builder = new StringBuilder();
        builder.append("{\"Version\":\"2012-10-17\","
                + "\"Statement\":[{\"Effect\":\"Allow\","
                + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:ListBucketMultipartUploads\",\"s3:GetBucketLocation\",\"s3:ListBucket\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "\"]},"
                + "{\"Effect\":\"Allow\"," + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}");

        return builder;
    }


}
