package ru.otus.spring.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "LibraryUserInfo", attributeNodes = @NamedAttributeNode("userRoles"))
@Table(name = "library_users")
public class LibraryUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_login", nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @ToString.Exclude
    @OneToMany(cascade= CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<LibraryUserRole> userRoles;
}
