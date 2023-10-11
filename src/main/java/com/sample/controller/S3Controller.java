package com.sample.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.sample.common.S3Client;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/s3")
public record S3Controller(S3Client s3Client) {

    @PostMapping(value = "/{bucketName}")
    public void createBucket(@PathVariable String bucketName, @RequestParam boolean publicBucket) {
        s3Client.createS3Bucket(bucketName, publicBucket);
    }

    @GetMapping(path = "/buckets")
    public List<String> listBuckets() {
        var buckets = s3Client.listBuckets();
        return buckets.stream().map(Bucket::getName).toList();
    }

    @DeleteMapping(value = "/{bucketName}")
    public void deleteBucket(@PathVariable String bucketName) {
        s3Client.deleteBucket(bucketName);
    }

    @PostMapping(value = "/{bucketName}/objects")
    public void createObject(@PathVariable String bucketName, @RequestBody JsonNode jsonNode, @RequestParam boolean publicObject) {
        // s3Client.putObject(bucketName, jsonNode, publicObject);
    }

    @GetMapping(value = "/{bucketName}/objects/{objectName}")
    public File downloadObject(@PathVariable String bucketName, @PathVariable String objectName) {
        s3Client.downloadObject(bucketName, objectName);
        return new File("./" + objectName);
    }

    @PatchMapping(value = "/{bucketSourceName}/objects/{objectName}/{bucketTargetName}")
    public void moveObject(@PathVariable String bucketSourceName, @PathVariable String objectName, @PathVariable String bucketTargetName) {
        s3Client.moveObject(bucketSourceName, objectName, bucketTargetName);
    }

    @GetMapping(value = "/{bucketName}/objects")
    public List<String> listObjects(@PathVariable String bucketName) {
        return s3Client.listObjects(bucketName).stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    @DeleteMapping(value = "/{bucketName}/objects/{objectName}")
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) {
        s3Client.deleteObject(bucketName, objectName);
    }

    @DeleteMapping(value = "/{bucketName}/objects")
    public void deleteObject(@PathVariable String bucketName, @RequestBody List<String> objects) {
        s3Client.deleteMultipleObjects(bucketName, objects);
    }
}
