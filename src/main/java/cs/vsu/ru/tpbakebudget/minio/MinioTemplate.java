package cs.vsu.ru.tpbakebudget.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MinioTemplate {

    private final MinioClient minioClient;

    private final String bucketName;

    @Autowired
    public MinioTemplate(@Qualifier("minioProperties") MinioProperties properties) {
        this.minioClient = MinioClient.builder()
                .endpoint(properties.getMinioUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        this.bucketName = properties.getBucketName();
        createBucketIfNotExists(bucketName);
    }

    public void uploadFile(ProductPicture picture) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String[] allowedExtensions = {"jpg", "jpeg", "png"};
        String fileExtension = picture.getMinioObjectName().substring(picture.getMinioObjectName().lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(allowedExtensions).contains(fileExtension)) {
            throw new IllegalArgumentException("Unsupported file type. Allowed file types are: " + Arrays.toString(allowedExtensions));
        }

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(picture.getMinioObjectName())
                .stream(new ByteArrayInputStream(picture.getBase64Image()), picture.getBase64Image().length, -1)
                .contentType("application/octet-stream")
                .build());
    }

    public InputStream downloadFile(String minioObjectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InternalException, InvalidResponseException, XmlParserException {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(minioObjectName)
                .build());
    }

    public void deleteFile(String minioObjectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InternalException, InvalidResponseException, XmlParserException {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(minioObjectName)
                .build());
    }

//    public String getPresignedUrl(String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InternalException, InvalidResponseException, XmlParserException {
//        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                .bucket(bucketName)
//                .object(objectName)
//                .method(Method.GET)
//                .build());
//    }

    public String getPresignedUrl(String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InternalException, InvalidResponseException, XmlParserException {
        String contentType;
        if (objectName.endsWith(".jpg") || objectName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (objectName.endsWith(".png")) {
            contentType = "image/png";
        } else {
            throw new IllegalArgumentException("Unsupported file type. Supported types are: jpg, jpeg, png");
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("response-content-type", contentType);

        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .extraQueryParams(queryParams)
                .build());
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            System.out.println("Error in createBucketIfNotExists" + e.getMessage());
        }
    }
}
