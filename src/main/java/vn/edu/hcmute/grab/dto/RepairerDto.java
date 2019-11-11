package vn.edu.hcmute.grab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairerDto {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String address;

    private String phone;

    private String avatar;

    private float rating = 0.0f;

    private long reviews = 0;

    private String major;

    private long completedJob = 0l;
}
