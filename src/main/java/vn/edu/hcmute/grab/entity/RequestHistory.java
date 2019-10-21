package vn.edu.hcmute.grab.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.ActionStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor
public class RequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionStatus status;

    private long point;

    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "repairer_id", nullable = false)
    private Repairer repairer;
}
