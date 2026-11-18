package com.alhourani.holdpoint.resource;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="resources")
@NoArgsConstructor
@Getter 
@Setter
@AllArgsConstructor
public class Resource {


    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "resource_key" , nullable = false , unique = true , length = 150)
    private String resourceKey;


    @Column(name = "name" , nullable = false  , length = 150)
    private String name;

    @Column(nullable = false)
    private  Long capacity;

    @Column(name = "reserved_quantity", nullable = false)
    private Long reservedQuantity = 0L;

    @Column(name = "confirmed_quantity", nullable = false)
    private Long confirmedQuantity = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    
    @PrePersist 
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

};
