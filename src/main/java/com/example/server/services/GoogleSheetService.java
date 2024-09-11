package com.example.server.services;

import com.example.server.entities.Projet;
import com.example.server.entities.Stagiaire;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetService {

    private static final String APPLICATION_NAME = "Stagiaire Management";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json"; // Adjust path as needed
    private static final String SPREADSHEET_ID = "1so24lEglO-IJoGplONBkOa1caL2eLQnoNaTkJ60OLbk"; // Your Spreadsheet ID
    private static final String SPREADSHEET_IDEncadrant = "1Vy0vaz1hWeOCqFA6MC6Ular_m7m2948VNmRTh7ssDWo"; // Your Spreadsheet ID
    private static final String SPREADSHEET_IDProjets = "1oPPxWDCVXgPYuyHagJ_M1-p5v73juNg335Q7YSAn_Qg"; // Your Spreadsheet ID
    private static final String SHEET_NAME = "Liste_des_stagiaires"; // Your sheet name
    private Sheets sheetsService;

    public GoogleSheetService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.sheetsService = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleSheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }



    public void printSheetContents() throws IOException {
        // Use a test range to debug
        String range = "!A1:G3"; // Ensure this matches your sheet name and range

        // Print debug information
        System.out.println("Range being used: " + range);

        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            // Print header

            // Print each row
            // Print each row
            for (List<Object> row : values) {
                // Convert each element of the row to String
                String rowOutput = String.join("\t", row.stream()
                        .map(Object::toString) // Convert each element to String
                        .toArray(String[]::new)); // Convert the stream to an array
                System.out.println(rowOutput);
            }
        }
    }
    public void addEncadrantToSheet(String nom, String prenom, String email, String telephone) throws IOException {
        List<List<Object>> values = Collections.singletonList(
                List.of(nom, prenom, email, telephone)
        );
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_IDEncadrant, "!A1", body)
                .setValueInputOption("RAW")
                .execute();
    }
    public void addProjectToSheet(Projet projet) throws IOException {
        List<List<Object>> values = new ArrayList<>();

        // Add project details
        List<Object> projectData = new ArrayList<>();
        projectData.add(projet.getTitre() != null ? projet.getTitre() : "No Title");
        projectData.add(projet.getDescription() != null ? projet.getDescription() : "No Description");
        projectData.add(projet.getDatedebut() != null ? projet.getDatedebut() : "No Start Date");
        projectData.add(projet.getDatefin() != null ? projet.getDatefin() : "No End Date");
        projectData.add(projet.getEtat() != null ? projet.getEtat() : "No State");
        projectData.add(projet.getSheeturl() != null ? projet.getSheeturl() : "No URL");

        // Add encadrant details
        if (projet.getEncadrant() != null) {
            projectData.add(projet.getEncadrant().getNom() != null ? projet.getEncadrant().getNom() : "No Name");
            projectData.add(projet.getEncadrant().getPrenom() != null ? projet.getEncadrant().getPrenom() : "No Prenom");
            projectData.add(projet.getEncadrant().getEmail() != null ? projet.getEncadrant().getEmail() : "No Email");
        } else {
            projectData.add("No Encadrant Assigned");
            projectData.add("No Encadrant Assigned");
            projectData.add("No Encadrant Assigned");
        }

        // Handle stagiaires details
        List<Stagiaire> stagiaires = projet.getStagiaires();
        if (stagiaires != null) {
            for (Stagiaire stagiaire : stagiaires) {
                List<Object> stagiaireData = new ArrayList<>(projectData); // Copy project and encadrant info
                stagiaireData.add(stagiaire.getNom() != null ? stagiaire.getNom() : "No Name");
                stagiaireData.add(stagiaire.getPrenom() != null ? stagiaire.getPrenom() : "No Prenom");
                stagiaireData.add(stagiaire.getEmail() != null ? stagiaire.getEmail() : "No Email");
                stagiaireData.add(stagiaire.getTelephone() != null ? stagiaire.getTelephone() : "No Telephone");
           //     stagiaireData.add(stagiaire.getFiliere() != null ? stagiaire.getFiliere() : "No Filiere");
                stagiaireData.add(stagiaire.getEcole() != null ? stagiaire.getEcole() : "No Ecole");
                stagiaireData.add(stagiaire.getNiveau() != null ? stagiaire.getNiveau() : "No Niveau");

                values.add(stagiaireData);
            }
        } else {
            // If there are no stagiaires, add only project data
            values.add(projectData);
        }

        // Debugging output
        System.out.println("Data to be sent to Google Sheets: " + values);

        // Append data to Google Sheets
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_IDProjets, "A1", body)
                .setValueInputOption("RAW")
                .execute();
    }


    public void addStagiaireToSheet(String nom, String prenom, String email, String telephone, String filiere, String ecole, String level) throws IOException {
        List<List<Object>> values = Collections.singletonList(
                List.of(nom, prenom, email, telephone, filiere, ecole, level)
        );
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, "!A1", body)
                .setValueInputOption("RAW")
                .execute();
    }
    @PostConstruct
    public void init() {
        try {
          addStagiaireToSheet("Doe", "John", "john.doe@example.com", "123-456-7890", "Computer Science", "XYZ University", "Junior");

            printSheetContents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
