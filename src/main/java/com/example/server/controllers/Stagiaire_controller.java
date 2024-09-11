package com.example.server.controllers;

import java.io.IOException;
import java.util.List;

import com.example.server.entities.Stagiaire;
import com.example.server.services.Stagiaire_Service;
import com.example.server.services.GoogleSheetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("stagiaire")
public class Stagiaire_controller {
	@Autowired
	Stagiaire_Service stagiaireService;

	@Autowired
	GoogleSheetService googleSheetService;

	@GetMapping()
	public List<Stagiaire> getAllStagiaire() {
		return stagiaireService.getall();
	}

	@GetMapping("/{id}")
	public Stagiaire getByIDStagiaire(@PathVariable Long id) {
		return stagiaireService.getById(id);
	}

	@PostMapping()
	public ResponseEntity<Stagiaire> Poststagiaire(@RequestBody Stagiaire stagiaire) {
		// Save to the database
		Stagiaire savedStagiaire = stagiaireService.save(stagiaire);

		// Save to Google Sheets
		try {
			googleSheetService.addStagiaireToSheet(
					stagiaire.getNom(),
					stagiaire.getPrenom(),
					stagiaire.getEmail(),
					stagiaire.getTelephone(),
					stagiaire.getFiliere() != null ? stagiaire.getFiliere().getFiliere() : "N/A",
					stagiaire.getFiliere() != null ? stagiaire.getFiliere().getEcole() : "N/A",
					stagiaire.getFiliere() != null ? stagiaire.getFiliere().getNiveau() : "N/A"

			);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Handle Google Sheets error
		}

		return new ResponseEntity<>(savedStagiaire, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public void DeleteStagiaire(@PathVariable Long id) {
		stagiaireService.delete(id);
	}

	@PutMapping({ "/{id}" })
	public ResponseEntity<Stagiaire> updateAdmin(@PathVariable("id") Long id, @RequestBody Stagiaire stagiaire) {
		stagiaireService.update(id, stagiaire);
		return new ResponseEntity<>(stagiaireService.getById(id), HttpStatus.OK);
	}
}
