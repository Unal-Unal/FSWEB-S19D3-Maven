package com.workintech.s19d2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles", schema = "bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // store as ROLE_USER or ROLE_ADMIN
    @Column(nullable = false, unique = true)
    private String authority;
}