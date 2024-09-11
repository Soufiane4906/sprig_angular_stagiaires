package com.example.server.controllers;

import com.example.server.entities.Projet;
import com.example.server.services.GoogleSheetService;
import com.example.server.services.Projet_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("projet")
public class Projet_controller {

    @Autowired
    Projet_Service projetService;

    @Autowired
    GoogleSheetService googleSheetService;

    // Get all projects
    @GetMapping()
    public List<Projet> getAllProjets() {
        return projetService.getAllProjets();
    }

    // Get a project by ID
    @GetMapping("/{id}")
    public Projet getByIDProjet(@PathVariable Long id) {
        return projetService.getProjetById(id);
    }

    // Add a new project
    @PostMapping()
    public ResponseEntity<Projet> postProjet(@RequestBody Projet projet) throws IOException {
        // Save to the database
        Projet savedProjet = projetService.saveProjet(projet);

        // Save project to Google Sheets
        try {
            googleSheetService.addProjectToSheet(savedProjet);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Handle Google Sheets error
        }

        return new ResponseEntity<>(savedProjet, HttpStatus.OK);
    }

    // Delete a project by ID
    @DeleteMapping("/{id}")
    public void deleteProjet(@PathVariable Long id) {
        projetService.deleteProjet(id);
    }

    // Update an existing project by ID
    @PutMapping("/{id}")
    public ResponseEntity<Projet> updateProjet(@PathVariable("id") Long id, @RequestBody Projet projet) throws IOException {
        projetService.saveProjet(projet); // Save updated project details
        return new ResponseEntity<>(projetService.getProjetById(id), HttpStatus.OK);
    }
}
