package com.minelog.shared.common.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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
