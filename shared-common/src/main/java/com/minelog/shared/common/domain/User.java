package com.minelog.shared.common.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends CustomAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_seq")
  @SequenceGenerator(
      name = "app_user_id_seq",
      sequenceName = "app_user_id_seq",
      allocationSize = 1
  )
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  private boolean enabled;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @ToString.Exclude
  private List<UserRole> roles = new ArrayList<>();

  public void addRole(Role role) {
    UserRole userRole = UserRole.builder()
        .user(this)
        .role(role)
        .build();
    this.roles.add(userRole);
  }

  public void removeRole(Role role) {
    this.roles.removeIf(ur -> ur.getRole().equals(role));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(this.username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
