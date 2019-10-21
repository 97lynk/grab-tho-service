package vn.edu.hcmute.grab.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    private Long userId;

    @Builder
    public RequestDto(Long id, String textDescription, String[] imagesDescription, String address, double longitude,
                      double latitude, LocalDateTime createAt, RequestStatus status, int noReceiver, int noQuote,
                      boolean feedBack, float rate, String comment, Long userId) {
        this.id = id;
        this.textDescription = textDescription;
        this.imagesDescription = imagesDescription;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.createAt = createAt;
        this.status = status;
        this.noReceiver = noReceiver;
        this.noQuote = noQuote;
        this.feedBack = feedBack;
        this.rate = rate;
        this.comment = comment;
        this.userId = userId;
    }
}
