package com.example.server.services;

import java.util.List;

import com.example.server.entities.Encadrant;
import com.example.server.repository.Encadrant_Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class Encadrant_Service implements Services_Dao<Encadrant> {
    @Autowired
    com.example.server.repository.Encadrant_Repository Encadrant_Repository;
    @Override
    public List<Encadrant> getall() {
        return Encadrant_Repository.findAll();
//		class Data {
//			
//		
//		}
//		return Encadrant_Repository.count();


    }

    @Override
    public Encadrant getById(Long id) {
        return Encadrant_Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not found"));

    }

    @Override
    public Encadrant save(Encadrant Encadrant) {
        return Encadrant_Repository.save(Encadrant);
    }

    @Override
    public void update(Long id, Encadrant Encadrant) {
        Encadrant Encadrant1 = Encadrant_Repository.findById(id).get();
        Encadrant1.setId(Encadrant.getId());
        Encadrant1.setNom(Encadrant.getNom());
        Encadrant1.setPrenom(Encadrant.getPrenom());
        Encadrant_Repository.save(Encadrant1);

    }

    @Override
    public void delete(Long id) {
        Encadrant_Repository.deleteById(id);
    }

}
