package vn.edu.hcmute.grab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.hcmute.grab.constant.RequestStatus;

@Data
@NoArgsConstructor
public class AddRequestDto {

    private String textDescription;

    private String[] imagesDescription;

    private String address;

    private double longitude;

    private double latitude;

}
