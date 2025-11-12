package br.com.grape.accessmanager.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.grape.accessmanager.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID = Auto Increment
    private Integer id;

    @Column(nullable = false, length = 255) // name = VARCHAR(255) NOT NULL
    private String name;

    @Column(nullable = false, unique = true, length = 255) // email = VARCHAR(255) NOT NULL UNIQUE
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255) // Mapeia para "password_hash"
    private String passwordHash;

    @Enumerated(EnumType.STRING) // JPA salvar o NOME do enum (ex: "ACTIVE")
    @Column(nullable = false)
    private UserStatus status;

    @CreationTimestamp // Gerenciado pelo Hibernate (Setado na criação)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp // Gerenciado pelo Hibernate (Setado a cada atualização)
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}