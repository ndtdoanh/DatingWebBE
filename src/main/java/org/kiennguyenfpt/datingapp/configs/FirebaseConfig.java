package org.kiennguyenfpt.datingapp.configs;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("https://s3.filebin.net/filebin/8fb415919cdbb393ea21796c80c78d351eed9de3bffb41a05475b2ed867ac108/029fb88d4d0fecbb1ddf7c4bdd6b84f106ef0d5725c682cb802ee331769ee650?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=7pMj6hGeoKewqmMQILjm%2F20241104%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20241104T190200Z&X-Amz-Expires=60&X-Amz-SignedHeaders=host&response-cache-control=max-age%3D60&response-content-disposition=filename%3D%22datingwebfpt-firebase-adminsdk-grvca-d65d9a7351.json%22&response-content-type=application%2Fjson&X-Amz-Signature=451fe0d756192241b5b4b8fe2715b6e6eab36a87cc22ad379f7dc7b76cfb0012"); //doanh
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("datingwebexe")
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
