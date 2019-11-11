package vn.edu.hcmute.grab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;

    private String textDescription;

    private String[] imagesDescription;

    private String address;

    private double longitude;

    private double latitude;

    private LocalDateTime createAt;

    private RequestStatus status;

    private int noReceiver;

    private int noQuote;

    private boolean feedBack;

    private float rate;

    private String comment;

    private long point;

    private Long userId;

    private String userFullName;

    private String userAvatar;

    private Long repairerId;
}
