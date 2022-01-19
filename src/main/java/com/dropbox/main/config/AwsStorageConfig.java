package com.dropbox.main.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsStorageConfig {

    private final String ACCESS_KEY_ID = "AKIA4BIFJKA5AJUHLKVM";
    private final String ACCESS_KEY = "GxftXs+/ePVYbcbyXer5cP/Wumb9v01vZmbVRzPx";
    private final String REGION = "ap-south-1";

    @Bean
    public AmazonS3 generateS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, ACCESS_KEY);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(REGION).build();
    }
}