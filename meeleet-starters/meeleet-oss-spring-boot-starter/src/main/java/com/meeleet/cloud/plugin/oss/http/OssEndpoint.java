package com.meeleet.cloud.plugin.oss.http;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.meeleet.cloud.plugin.oss.service.OssTemplate;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * oss 对外提供服务端点
 *
 * @author lengleng
 * @author 858695266
 * <p>
 * oss.info
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${oss.http.prefix:}/oss")
@Api(tags = "oss:http接口")
@Tag(name = "OssEndpoint", description = "oss:http接口")
public class OssEndpoint {

	/**
	 * OSS操作模板
	 */
	private final OssTemplate ossTemplate;

	/**
	 * Bucket Endpoints
	 */
	@PostMapping("/bucket/{bucketName}")
	public Bucket createBucket(@PathVariable @NotBlank String bucketName) {
		ossTemplate.createBucket(bucketName);
		return ossTemplate.getBucket(bucketName).get();
	}

	@GetMapping("/bucket")
	public List<Bucket> getBuckets() {
		return ossTemplate.getAllBuckets();
	}

	@GetMapping("/bucket/{bucketName}")
	public Bucket getBucket(@PathVariable @NotBlank String bucketName) {
		return ossTemplate.getBucket(bucketName)
				.orElseThrow(() -> new IllegalArgumentException("Bucket Name not found!"));
	}

	@DeleteMapping("/bucket/{bucketName}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteBucket(@PathVariable @NotBlank String bucketName) {
		ossTemplate.removeBucket(bucketName);
	}

	/**
	 * Object Endpoints
	 */
	@SneakyThrows
	@PostMapping("/object/{bucketName}")
	public S3ObjectSummary createObject(@RequestBody @NotNull MultipartFile object,
			@PathVariable @NotBlank String bucketName) {
		@Cleanup
		InputStream inputStream = object.getInputStream();
		String name = object.getOriginalFilename();

		ossTemplate.putObject(bucketName, name, inputStream, object.getSize(), object.getContentType());
		S3Object objectInfo = ossTemplate.getObjectInfo(bucketName, name);
		ObjectMetadata objectMetadata = objectInfo.getObjectMetadata();
		S3ObjectSummary objectSummary = new S3ObjectSummary();
		objectSummary.setKey(objectInfo.getKey());
		objectSummary.setBucketName(bucketName);
		objectSummary.setETag(objectMetadata.getETag());
		objectSummary.setLastModified(objectMetadata.getLastModified());
		objectSummary.setSize(objectMetadata.getContentLength());
		return objectSummary;
	}

	/**
	 * Object Endpoints
	 */
	@SneakyThrows
	@PostMapping("/object/{bucketName}/{objectName}")
	public S3ObjectSummary createObject(@RequestBody @NotNull MultipartFile object,
			@PathVariable @NotBlank String bucketName, @PathVariable @NotBlank String objectName) {
		@Cleanup
		InputStream inputStream = object.getInputStream();
		ossTemplate.putObject(bucketName, objectName, inputStream, object.getSize(), object.getContentType());
		S3Object objectInfo = ossTemplate.getObjectInfo(bucketName, objectName);
		ObjectMetadata objectMetadata = objectInfo.getObjectMetadata();
		S3ObjectSummary objectSummary = new S3ObjectSummary();
		objectSummary.setKey(objectInfo.getKey());
		objectSummary.setBucketName(bucketName);
		objectSummary.setETag(objectMetadata.getETag());
		objectSummary.setLastModified(objectMetadata.getLastModified());
		objectSummary.setSize(objectMetadata.getContentLength());
		return objectSummary;
	}

	@GetMapping("/object/{bucketName}/{objectName}")
	public List<S3ObjectSummary> filterObject(@PathVariable @NotBlank String bucketName,
			@PathVariable @NotBlank String objectName) {
		return ossTemplate.getAllObjectsByPrefix(bucketName, objectName);
	}

	@GetMapping("/object/{bucketName}/{objectName}/{expires}")
	public Map<String, Object> getObjectUrl(@PathVariable @NotBlank String bucketName,
			@PathVariable @NotBlank String objectName, @PathVariable @NotNull Integer expires) {
		Map<String, Object> responseBody = new HashMap<>(8);
		// Put Object info
		responseBody.put("bucket", bucketName);
		responseBody.put("object", objectName);
		responseBody.put("url", ossTemplate.getObjectURL(bucketName, objectName, expires));
		responseBody.put("expires", expires);
		return responseBody;
	}

	@GetMapping("/object/put/{bucketName}/{objectName}/{expires}")
	public Map<String, Object> getPutObjectUrl(@PathVariable @NotBlank String bucketName,
			@PathVariable @NotBlank String objectName, @PathVariable @NotNull Integer expires) {
		Map<String, Object> responseBody = new HashMap<>(8);
		// Put Object info
		responseBody.put("bucket", bucketName);
		responseBody.put("object", objectName);
		responseBody.put("url", ossTemplate.getPutObjectURL(bucketName, objectName, expires));
		responseBody.put("expires", expires);
		return responseBody;
	}

	@ResponseStatus(HttpStatus.ACCEPTED)
	@DeleteMapping("/object/{bucketName}/{objectName}/")
	public void deleteObject(@PathVariable @NotBlank String bucketName, @PathVariable @NotBlank String objectName) {
		ossTemplate.removeObject(bucketName, objectName);
	}

}
