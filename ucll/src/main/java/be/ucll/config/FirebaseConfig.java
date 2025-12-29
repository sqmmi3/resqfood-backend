package be.ucll.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import be.ucll.exception.FirebaseInitializationException;

@Configuration
public class FirebaseConfig {
  
  @Value("${firebase.service-account-path:serviceAccountKey.json}")
  private String serviceAccountPath;

  @Bean
  public FirebaseApp firebaseApp() {
    if (!FirebaseApp.getApps().isEmpty()) {
      return FirebaseApp.getInstance();
    }

    try {
      InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(serviceAccountPath);

      if (serviceAccount == null) {
        throw new IllegalStateException("Firebase service account not found: " + serviceAccountPath);
      }

      FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

      return FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      throw new FirebaseInitializationException("Failed to load Firebase service account", e);
    }
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
    return FirebaseMessaging.getInstance(firebaseApp);
  }
}
