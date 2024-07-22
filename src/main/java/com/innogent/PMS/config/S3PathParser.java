package com.innogent.PMS.config;

import org.springframework.stereotype.Component;

@Component
public class S3PathParser {
    public String getKey(String s3Path) {
        if (s3Path == null || !s3Path.startsWith("s3://")) {
            throw new IllegalArgumentException("Invalid S3 path");
        }

        // Remove the "s3://" prefix
        String pathWithoutScheme = s3Path.substring(5);

        // Split into bucket name and key
        int slashIndex = pathWithoutScheme.indexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 path: no key found");
        }

        String bucketName = pathWithoutScheme.substring(0, slashIndex);
        String key = pathWithoutScheme.substring(slashIndex + 1);

        return key;
    }
}
