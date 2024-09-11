package com.example.server.entities;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Encadrant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Relationships
    @OneToMany(mappedBy = "encadrant", cascade = CascadeType.ALL)
    private List<Projet> projets;
}
