package org.example;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static java.nio.file.Paths.get;

public class S3Service {

    private final Logger logger = Logger.getLogger(S3Service.class.getName());
    private final AmazonS3 amazonS3;

    public S3Service() {
        this.amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }

    public String createBucket(String bucketName) {
        amazonS3.createBucket(bucketName);
        return "Success";
    }

    public String downloadFileContent(String bucketName, String filename) {
        try {
            S3Object object = amazonS3.getObject(bucketName, filename);
            byte[] bytes = object.getObjectContent().readAllBytes();
            return new String(bytes);
        } catch (IOException e) {
            logger.info("Error encountered while downloading object from " + bucketName
                    + " with error " + e.getMessage());
        }
        return null;
    }

    public void putObject(String bucketName, File file, String keyName) {
        amazonS3.putObject(bucketName, keyName, file);
    }

}
