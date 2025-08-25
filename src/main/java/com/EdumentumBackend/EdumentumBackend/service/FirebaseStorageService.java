package com.EdumentumBackend.EdumentumBackend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseStorageService {

    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
            String url = String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(), fileName
            );
            return CompletableFuture.completedFuture(url);
        } catch (IOException e) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
