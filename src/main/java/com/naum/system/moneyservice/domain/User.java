package com.naum.system.moneyservice.domain;

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
    @Column(name = "user_id")
    Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password")
    private String password;
}
