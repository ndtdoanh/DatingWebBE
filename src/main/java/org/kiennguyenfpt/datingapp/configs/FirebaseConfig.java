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
                new FileInputStream("D:\\ki 8\\datingwebfpt-firebase-adminsdk-grvca-d65d9a7351.json"); //doanh
//                new FileInputStream("D:\\Kien FPT\\datingwebfpt-firebase-adminsdk-grvca-d65d9a7351.json"); //kien
//                new FileInputStream("C:\\Users\\Acer\\Downloads\\datingwebfpt-firebase-adminsdk-grvca-d65d9a7351.json"); //huy
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                //.setStorageBucket("datingapp-f4ecb.appspot.com")
                .setStorageBucket("datingwebexe")
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
