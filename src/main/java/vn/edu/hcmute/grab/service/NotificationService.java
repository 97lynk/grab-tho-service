package vn.edu.hcmute.grab.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.dto.NotificationDto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class NotificationService {

    private DatabaseReference notificationDb;

    private Environment environment;

    @Autowired
    public NotificationService(Environment environment) {
        this.environment = environment;

        ///motel-242404-firebase-adminsdk-wzfw7-338fd655b6.json

        try {
            InputStream  serviceAccount = getClass().getResourceAsStream("/motel-242404-firebase-adminsdk-wzfw7-338fd655b6.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://motel-242404.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);

//            FirebaseAuth defaultAuth = FirebaseAuth.getInstance();
//            FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance("fcmTokens");

            log.info("notifications db " + environment.getActiveProfiles()[0] + "/notifications");
            notificationDb = FirebaseDatabase
                    .getInstance()
                    .getReference(environment.getActiveProfiles()[0] + "/notifications");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void saveNotification(String username, NotificationDto notification) {
        DatabaseReference usersRef = notificationDb.child(username);
        usersRef.push().setValueAsync(notification);
    }
}
