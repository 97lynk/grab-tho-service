package vn.edu.hcmute.grab.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import vn.edu.hcmute.grab.constant.RoleName;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    private String email;

    private String fullName;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    private String address;

    private String phone;

    private boolean block;

    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<Request> requests;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(
                roles.stream()
                        .map(Role::getName)
                        .map(RoleName::name)
                        .toArray(String[]::new)
        );
    }

    @Builder
    public User(@NotBlank String username, String email, String fullName, @NotBlank @Size(min = 6, max = 100) String password, String address, String phone, boolean block, String avatar, List<Role> roles, List<Request> requests) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.block = block;
        this.avatar = avatar;
        this.roles = roles;
        this.requests = requests;
    }
}
