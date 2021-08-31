package ru.otus.spring.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.spring.security.UserRole;
import ru.otus.spring.util.UserRoleConverter;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "library_user_roles")
public class LibraryUserRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Convert(converter = UserRoleConverter.class)
    @Column(name = "role_name", nullable = false)
    private UserRole role;
}
