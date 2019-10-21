package vn.edu.hcmute.grab.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Anh Tuan
 * @createdDate 5/7/19
 * @content
 */
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageProperties {

    private String uploadDir;

}
