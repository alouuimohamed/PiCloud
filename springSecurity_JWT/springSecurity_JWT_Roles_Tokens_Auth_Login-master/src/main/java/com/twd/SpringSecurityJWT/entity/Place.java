package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private boolean isReserved;
}
