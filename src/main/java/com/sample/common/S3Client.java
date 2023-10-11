package com.sample.common;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j(topic = "S3-SERVICE")
public class S3Client {

    @Autowired
    private AmazonS3 amazonS3Client;

    public void createS3Bucket(String bucketName, boolean publicBucket) {
        if (amazonS3Client.doesBucketExistV2(bucketName)) {
            log.info("Bucket name already in use. Try another name.");
            return;
        }
        if (publicBucket) {
            amazonS3Client.createBucket(bucketName);
        } else {
            amazonS3Client.createBucket(new CreateBucketRequest(bucketName).withCannedAcl(CannedAccessControlList.Private));
        }
    }

    public List<Bucket> listBuckets() {
        return amazonS3Client.listBuckets();
    }

    public void deleteBucket(String bucketName) {
        try {
            amazonS3Client.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            return;
        }
    }

    public String putObject(String bucketName, MultipartFile file, boolean publicObject) {
        log.info("Pushing file {} to amazon s3", file.getName());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        String key = new LocalDate() + "-" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");

        try {
            PutObjectRequest putObjectRequest;
            if (publicObject) {
                putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.PublicRead);
            } else {
                putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.Private);
            }
            amazonS3Client.putObject(putObjectRequest);
            log.info("File {} has pushed successful", file.getName());
            return amazonS3Client.getUrl(bucketName, key).getPath();
        } catch (Exception e) {
            log.error("Pushing file was failure, message={}", e.getMessage(), e);
            throw new RuntimeException("Pushing file was failure");
        }
    }

    public List<S3ObjectSummary> listObjects(String bucketName) {
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
        return objectListing.getObjectSummaries();
    }

    public void downloadObject(String bucketName, String objectName) {
        S3Object s3object = amazonS3Client.getObject(bucketName, objectName);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        try {
            FileUtils.copyInputStreamToFile(inputStream, new File("." + File.separator + objectName));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteObject(String bucketName, String objectName) {
        amazonS3Client.deleteObject(bucketName, objectName);
    }

    public void deleteMultipleObjects(String bucketName, List<String> objects) {
        DeleteObjectsRequest delObjectsRequests = new DeleteObjectsRequest(bucketName)
                .withKeys(objects.toArray(new String[0]));
        amazonS3Client.deleteObjects(delObjectsRequests);
    }

    public void moveObject(String bucketSourceName, String objectName, String bucketTargetName) {
        amazonS3Client.copyObject(
                bucketSourceName,
                objectName,
                bucketTargetName,
                objectName
        );
    }

    private String getFileName(String fileName) {
        return new LocalDate() + "-" + fileName.replace(" ", "_");
    }
}
