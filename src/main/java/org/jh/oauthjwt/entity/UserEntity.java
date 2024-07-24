package org.jh.oauthjwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jh.oauthjwt.todo.domain.Todo;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;
    private String username;
    private String password;

    private String role;

    private String verificationCode;
    private boolean isVerified = false;

    private String provider;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();
}
