package com.EdumentumBackend.EdumentumBackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount =
                new ClassPathResource("google-services.json").getInputStream();

        Builder builder = new Builder();
        builder.setCredentials(GoogleCredentials.fromStream(serviceAccount));
        builder.setStorageBucket("project-64de8.appspot.com");
        FirebaseOptions options = builder
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
