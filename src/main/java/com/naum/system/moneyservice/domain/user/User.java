package com.naum.system.moneyservice.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "user_account")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false)
    private String email;
}
