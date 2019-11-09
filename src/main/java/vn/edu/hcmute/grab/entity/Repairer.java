package vn.edu.hcmute.grab.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
@NoArgsConstructor
public class Repairer  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float rating = 0.0f;

    private long reviews = 0;

    private String major = "";

    private long completedJob = 0l;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy="repairer", fetch = FetchType.LAZY)
    private List<RequestHistory> requestHistories;
}
