package vn.edu.hcmute.grab.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String textDescription;

    private String[] imagesDescription;

    private String address;

    private double longitude;

    private double latitude;

    private LocalDateTime createAt;

    @Enumerated(value = EnumType.STRING)
    private RequestStatus status;

    private int noReceiver;

    private int noQuote;

    private long point;

    private boolean feedBack;

    private float rate;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy="request", fetch = FetchType.LAZY)
    private List<RequestHistory> requestHistories;

    @Builder
    public Request(String textDescription, String[] imagesDescription, String address, double longitude, double latitude, LocalDateTime createAt, RequestStatus status, boolean feedBack, float rate, String comment, User user) {
        this.textDescription = textDescription;
        this.imagesDescription = imagesDescription;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.createAt = createAt;
        this.status = status;
        this.feedBack = feedBack;
        this.rate = rate;
        this.comment = comment;
        this.user = user;
    }
}
