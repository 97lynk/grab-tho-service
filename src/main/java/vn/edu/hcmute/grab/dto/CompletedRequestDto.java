package vn.edu.hcmute.grab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedRequestDto {

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

    private int point;

    private boolean feedBack;

    private float rate;

    private String comment;

    private String repairerName;

    private Long userId;

}
