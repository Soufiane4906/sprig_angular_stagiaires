package com.example.server.controllers;

import com.example.server.entities.Encadrant;
import com.example.server.services.GoogleSheetService;
import com.example.server.services.Encadrant_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("encadrant")
public class Encadrant_controller {
	@Autowired
	Encadrant_Service encadrantService;

	@Autowired
	GoogleSheetService googleSheetService;

	@GetMapping()
	public List<Encadrant> getAllEncadrant() {
		return encadrantService.getall();
	}

	@GetMapping("/{id}")
	public Encadrant getByIDEncadrant(@PathVariable Long id) {
		return encadrantService.getById(id);
	}

	@PostMapping()
	public ResponseEntity<Encadrant> Postencadrant(@RequestBody Encadrant encadrant) {
		// Save to the database
		Encadrant savedEncadrant = encadrantService.save(encadrant);

		// Save to Google Sheets
		try {
			googleSheetService.addEncadrantToSheet(
					encadrant.getNom(),
					encadrant.getPrenom(),
					encadrant.getEmail(),
					encadrant.getTelephone()


			);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Handle Google Sheets error
		}

		return new ResponseEntity<>(savedEncadrant, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public void DeleteEncadrant(@PathVariable Long id) {
		encadrantService.delete(id);
	}

	@PutMapping({ "/{id}" })
	public ResponseEntity<Encadrant> updateAdmin(@PathVariable("id") Long id, @RequestBody Encadrant encadrant) {
		encadrantService.update(id, encadrant);
		return new ResponseEntity<>(encadrantService.getById(id), HttpStatus.OK);
	}
}
