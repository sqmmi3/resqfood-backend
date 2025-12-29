package be.ucll.config;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

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
        System.out.println("⚠️ WARNING: Firebase service account not found at " + serviceAccountPath);
        System.out.println("⚠️ Proceeding in Offline-Notification mode...");
        return null; 
      }

      FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

      return FirebaseApp.initializeApp(options);
    } catch (Exception e) {
      System.out.println("⚠️ WARNING: Failed to initialize Firebase (Key might be dummy/invalid).");
      System.out.println("⚠️ Proceeding in Offline-Notification mode...");
      return null;
    }
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(@Nullable FirebaseApp firebaseApp) {
    if (firebaseApp == null) {
      return null;
    }
    return FirebaseMessaging.getInstance(firebaseApp);
  }
}