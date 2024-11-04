package org.kiennguyenfpt.datingapp.configs;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        final String jsonConfig = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"datingwebfpt\",\n" +
                "  \"private_key_id\": \"d65d9a73510fe813c05c1fcc05f193d8788f554c\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDGsoR8u7w5+Jzq\\nMcpl25XRv0t3kBF4UyWnd3sspJEcTlcs6LrePukZrDX/aJAoGLmoG/EI89gs1Ez5\\ndney9KrI22aMaFTBeLNuAzsRjY9UEJQaAwb1FkNh2HtMdI1F6e1y7dkCIdUz5c8Z\\nzc2FYrhRACBTefxTe1eRFNd9oU3iPVnzZri5gdJNAsH3rVVha0W0jLhVCZ8nmbQR\\n4nO0aIlcWiikZV/YrcLAU81RMGNi0Khi5di4iZG+1m/G3WdWK0b7wmJ0GSyb3Dvs\\nsh9/GQWngJ1IWEfsW8bHYQrkid4txIaWoUsuDf1KQ1SBvzcb+87RUXCYoKgb9nOO\\n2omBABJHAgMBAAECggEAN8/AECtJiT0QkmXu9KJz2Ekz4P8qMRKl4n7YrecBzaIh\\nsX8jsjaYkjoBpt7Pkzuytoo2r62SvLDbeDzzDWTVLfsWuHy7xeZp/A5TQrBxIItR\\nlmbgRup8fdjpdUXNwuQc6untSUCImv3DqPtabcuXe0UeT2RV4HsmUx24E9VBw7i6\\nMVbIbsOeWlvOnfKH7KaQBV79hNVfbcgJ7bNjShFXL8tVrF01T9Q5mRHyHMllA2/h\\nSSJGWiANgHMwcyjdjz+VRpPBX/M2WhgEV+L9E4aRihgX84NQ5Uo9PIURL0hl9BSe\\n7Il6BPGDgzfABdcshIs/9zwzlGvsR/0gjN68YfkqeQKBgQDn+UADEpHQeD5N58LU\\nPNftYj6i9iTPUHoOkfiDfsAedp+qWYZs4T4hUW+zO4q88Q2bNde46t0C5gPRvclL\\nQtKO0WkUnCeR9+mwcg6HisYOW4AURy01hurZPSucQJyjueoRrAEAddmJMfuAEgZA\\nbH2DXPNH5TmITCR/1XLVjONjDwKBgQDbRvNrDmxEo4rt0Wrpu2ZlfCu1NXetEI7z\\nv7aGk1aIJ6/yE4KibsvKcI8oYJjBKxVAGhJhPg4FUUIiykdhXRy7nK6CtrEboIOr\\nEbyeefUtyadLt3PW6oocxu2BGnyE01B2FRy8BlwRLki+i8+ne/rAfGWEpk9JFn6t\\nvT1FNI39SQKBgQC6/Ewf7ekBrNsz+vwuDjlTjRzGSTjUr58jfYBfpl0AcUg0jWNx\\nAoMNYlJynC0yLfB0ywrvcMnkGzq654uJkfydyKk2RsUSwU5qvxV9JlyNmC3+qn0D\\np5RItikgE2jwBN1rj0VjdJt8HoEG9AB8D7eAmv0zV89vUUkejhS5lI55+wKBgQDP\\nXofxZJBrUSafxX6QUFOEAivrkQb+ujwWs+VP26Ln3HB+RIk0SAeeCVi5XLDZbX8J\\nQh7Dq8XirXgYIqfiogX8Py6iG1fewqwCaiGw1bFSffQinPmF9ARtNouBnqTHkHf1\\nU/36qyECdn0BBoN45ry6sGnpSsaMXiOq9t4sxojJUQKBgQC5IZb2Sfv3vz2Bm/Uf\\nsxVJDizW014kyAXwznXhSYdElhzzTYbN+uiYAjLbS+eCm2YYSgFDi+ZwJ1p8SngT\\naF7CbKl++F/lzP5dfit0PDbvvKwxS8IS+g3zfyc/jjtQ0/f6vhYwxn2S8Z9KPwMl\\nt91o0xSw5a6TllhDUcdxh3crdQ==\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-grvca@datingwebfpt.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"110221077392389224887\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-grvca%40datingwebfpt.iam.gserviceaccount.com\",\n" +
                "  \"universe_domain\": \"googleapis.com\"\n" +
                "}";

        InputStream serviceAccount = new ByteArrayInputStream(jsonConfig.getBytes(StandardCharsets.UTF_8));
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("datingwebexe")
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
