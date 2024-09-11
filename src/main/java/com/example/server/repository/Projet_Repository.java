package com.example.server.repository;

import com.example.server.entities.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Projet_Repository extends JpaRepository<Projet, Long> {
}
