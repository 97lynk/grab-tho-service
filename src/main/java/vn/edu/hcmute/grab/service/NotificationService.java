package vn.edu.hcmute.grab.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.dto.NotificationDto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    DatabaseReference notificationDb;

    public NotificationService() {

        ///motel-242404-firebase-adminsdk-wzfw7-338fd655b6.json

        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream(
                    getClass().getClassLoader().getResource("motel-242404-firebase-adminsdk-wzfw7-338fd655b6.json").getFile()
            );

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://motel-242404.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);

//            FirebaseAuth defaultAuth = FirebaseAuth.getInstance();
//            FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance("fcmTokens");

            notificationDb = FirebaseDatabase
                    .getInstance()
                    .getReference("notifications");


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
