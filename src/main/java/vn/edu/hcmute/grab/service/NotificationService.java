package vn.edu.hcmute.grab.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.edu.hcmute.grab.dto.NotificationDto;
import vn.edu.hcmute.grab.entity.Request;
import vn.edu.hcmute.grab.entity.Setting;
import vn.edu.hcmute.grab.repository.SettingRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    private DatabaseReference notificationDb;

    private DatabaseReference fcmTokensDb;

    private final SettingRepository settingRepository;

    private Environment environment;

    @Autowired
    public NotificationService(SettingRepository settingRepository, Environment environment) {
        this.settingRepository = settingRepository;
        this.environment = environment;

        ///motel-242404-firebase-adminsdk-wzfw7-338fd655b6.json

        try {
            InputStream serviceAccount = getClass().getResourceAsStream("/motel-242404-firebase-adminsdk-wzfw7-338fd655b6.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://motel-242404.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);

//            FirebaseAuth defaultAuth = FirebaseAuth.getInstance();
//            FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance("fcmTokens");

            log.info("notifications db " + environment.getActiveProfiles()[0] + "/notifications");

            notificationDb = FirebaseDatabase.getInstance().getReference(environment.getActiveProfiles()[0] + "/notifications");
            fcmTokensDb = FirebaseDatabase.getInstance().getReference(environment.getActiveProfiles()[0] + "/fcmTokens");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void saveNotification(String username, NotificationDto notification) {
        Setting setting = settingRepository.findByUserUsername(username).orElse(null);
        if (setting != null && !setting.isNotification()) return;
        DatabaseReference usersRef = notificationDb.child(username);
        usersRef.push().setValueAsync(notification);
    }

    void saveNotificationWithoutSetting(String username, NotificationDto notification) {
        DatabaseReference usersRef = notificationDb.child(username);
        usersRef.push().setValueAsync(notification);
    }

    void pushNotification(final List<String> receivers, Notification notification, Request request) {
        List<String> receiversToDel = new ArrayList<>();
        List<Setting> settings = settingRepository.findAllByUserUsernameIn(receivers);
        receivers.forEach(r -> {
            Setting setting = settings.stream().filter(s -> r.equals(s.getUser().getUsername())).findAny().orElse(null);
            if (setting != null && !setting.isPushNotification()) receiversToDel.remove(r);
        });
        receivers.removeAll(receiversToDel);

        log.info("Send notification to {}", receivers);
        fcmTokensDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // key = username, value = token
                Map<String, String> fcmTokens = (Map<String, String>) snapshot.getValue();
                // filtering tokens of receivers
                List<String> tokens = fcmTokens.entrySet().stream()
                        .filter(t -> receivers.contains(t.getKey()))
                        .map(Map.Entry::getValue)
                        .distinct()
                        .collect(Collectors.toList());

                MulticastMessage message = MulticastMessage.builder()
                        .setNotification(notification)
                        .putData("requestId", request.getId().toString())
                        .addAllTokens(tokens)
                        .build();

                try {
                    FirebaseMessaging.getInstance().sendMulticast(message);
                    log.info("Sent notification success {}/{}", tokens.size(), receivers.size());
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
                fcmTokensDb.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                fcmTokensDb.removeEventListener(this);
            }
        });
    }
}
