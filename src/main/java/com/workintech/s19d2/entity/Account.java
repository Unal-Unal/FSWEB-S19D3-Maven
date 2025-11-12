package com.workintech.s19d2.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts", schema = "bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}