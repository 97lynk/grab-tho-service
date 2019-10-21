package vn.edu.hcmute.grab.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AcceptedRequestDto {


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

    private Long userId;

    private int point;

    private String repairerName;

    @Builder
    public AcceptedRequestDto(Long id, String textDescription, String[] imagesDescription, String address,
                              double longitude, double latitude, LocalDateTime createAt, RequestStatus status,
                              int noReceiver, int noQuote, Long userId, int point, String repairerName) {
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
        this.userId = userId;
        this.point = point;
        this.repairerName = repairerName;
    }
}
