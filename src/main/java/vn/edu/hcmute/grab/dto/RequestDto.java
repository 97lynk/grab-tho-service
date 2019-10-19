package vn.edu.hcmute.grab.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@Entity
@Table
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String textDescription;

    private String[] imagesDescription;

    private Location location;

    private LocalDateTime createAt;

    @Enumerated(value = EnumType.STRING)
    private RequestStatus status;

    private boolean feedBack;

    private float rate;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User users;
}
