package com.example.server.services;

import com.example.server.entities.Projet;
import com.example.server.repository.Projet_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class Projet_Service {

    @Autowired
    private Projet_Repository projetRepository;

    @Autowired
    private GoogleSheetService googleSheetService;

    public Projet saveProjet(Projet projet) throws IOException {
        // Save the project to the database
        Projet savedProjet = projetRepository.save(projet);

        // Save the project and its related stagiaires and encadrant to Google Sheets
        googleSheetService.addProjectToSheet(savedProjet);

        return savedProjet;
    }

    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    public Projet getProjetById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projet not found"));
    }

    public void deleteProjet(Long id) {
        projetRepository.deleteById(id);
    }
}
