package com.example.server.repository;

import com.example.server.entities.Encadrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Encadrant_Repository extends JpaRepository<Encadrant, Long> {
}
