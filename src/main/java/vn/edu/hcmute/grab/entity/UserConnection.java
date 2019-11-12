package vn.edu.hcmute.grab.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@IdClass(UserConnection.ProjectId.class)
@Table(name = "UserConnection", uniqueConstraints = {
        @UniqueConstraint(name = "UserConnectionRank", columnNames = { "userId", "providerId", "rank" }),
        @UniqueConstraint(name = "providerUser", columnNames = { "providerId", "providerUserId" }) })
public class UserConnection {

    @Id
    @Column(name = "USERID")
    private String userId;

    @Id
    @Column(name = "PROVIDERID")
    private String providerId;

    @Id
    @Column(name = "PROVIDERUSERID")
    private String providerUserId;

    @Column(name = "RANK")
    private int rank;

    @Column(name = "DISPLAYNAME")
    private String displayName;

    @Column(name = "PROFILEURL", length = 512)
    private String profileUrl;

    @Column(name = "IMAGEURL", length = 512)
    private String imageUrl;

    @Column(name = "ACCESSTOKEN")
    private String accessToken;

    @Column(name = "SECRET")
    private String secret;

    @Column(name = "REFRESHTOKEN")
    private String refreshToken;

    @Column(name = "EXPIRETIME")
    private Long expireTime;

    @Data
    public static class ProjectId implements Serializable {
        private static final long serialVersionUID = 1L;

        String userId;
        String providerId;
        String providerUserId;
    }
}