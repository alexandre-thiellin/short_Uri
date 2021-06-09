package com.example.shorturi.controller;

import com.example.shorturi.model.AssociatedUri;
import com.example.shorturi.model.MyUserDetails;
import com.example.shorturi.model.User;
import com.example.shorturi.repository.AssociatedUriRepository;
import com.example.shorturi.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.internal.JsonFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Project ShortUri
 *
 * @author Alexandre
 * This class is used to manage objects of type AssociatedUri
 **/
public class ShortUriManager {

    public static ShortUriManager INSTANCE;
    private final AssociatedUriRepository associatedUriRepository;
    private final UserRepository userRepository;

    private ShortUriManager(AssociatedUriRepository associatedUriRepository, UserRepository userRepository){
        this.associatedUriRepository = associatedUriRepository;
        this.userRepository = userRepository;
    }

    public static ShortUriManager getInstance(AssociatedUriRepository associatedUriRepository, UserRepository userRepository) {
        if(INSTANCE == null) {
            INSTANCE = new ShortUriManager(associatedUriRepository, userRepository);
        }
        return INSTANCE;
    }

    /**
     * Create a short uri from a full uri
     * @param longUri A string representing a full uri example : "https://example.com"
     * @return A string of length 8
     **/
    public String createShortUri(String longUri) {
        // hashing of the long uri
        CRC32 crc32 = new CRC32();
        crc32.update(longUri.getBytes());
        return Long.toHexString(crc32.getValue());
    }

    /**
     * This method create an AssociatedUri, format the response and return the response to the controller
     * @param associatedUri The AssociatedUri passed in the body of the post request (POST /api/v1/short_uris)
     *                      in fact something of the form : { "longUri" : https://something.com }
     * @param authentication The authentication informations of the user
     * @return A ResponseEntity with a string formatted to Json representing the AssociatedUri and other datas
     **/
    public ResponseEntity<String> createAssociatedUri(AssociatedUri associatedUri, Authentication authentication){

        String response;

        // Verifying the format of the request body
        if(associatedUri.getLongUri() == null){

            // plus 7200 seconds (2H) for the time zone of Paris
            String error = jsonError(400, null);

            return ResponseEntity.status(400).body(JsonFormatter.prettyPrint(error));
        }

        int status;

        // If the format is correct, verifying if the AssociatedUri already exist
        // if it exist return it if not create an AssociatedUri with the correct datas
        if(!associatedUriRepository.existsByLongUri(associatedUri.getLongUri())){

            // plus 7200 seconds (2H) for the time zone of Paris
            Instant now = Instant.now().plusSeconds(7200);


            User user = userRepository.findByEmail(((MyUserDetails) authentication.getPrincipal()).getUsername()).get();

            AssociatedUri newAssociatedUri = new AssociatedUri(0, user.getId(), createShortUri(associatedUri.getLongUri()), associatedUri.getLongUri(), 0, now.toString(), now.toString());
            associatedUriRepository.save(newAssociatedUri);

            // adding the "sho.rt/" to the shortId to get the short uri
            newAssociatedUri.setShortId("sho.rt/" + newAssociatedUri.getShortId());

            status = 201;

            response = jsonResponse(status, newAssociatedUri);
        } else {

            AssociatedUri retrievedAssociatedUri = associatedUriRepository.findByLongUri(associatedUri.getLongUri()).get();

            // adding the "sho.rt/" to the shortId to get the short uri
            retrievedAssociatedUri.setShortId("sho.rt/" + retrievedAssociatedUri.getShortId());

            status = 200;

            response = jsonResponse(status, retrievedAssociatedUri);
        }
        return ResponseEntity.status(status).body(JsonFormatter.prettyPrint(response));
    }

    /**
     * Retrieve an AssociatedUri from the database
     * if the AssociatedUri is retrieved the number of visitors is incremented and saved on the database
     * @param shortId A String of 8 characters
     * @return A ResponseEntity with the desired datas in a Json formatted string
     **/
    public ResponseEntity<String> getAssociatedUri(String shortId){

        AssociatedUri retrieved = associatedUriRepository.findByShortId(shortId).orElse(null);

        if(retrieved != null){
            // incrementing the number of visitors and setting the "timestamp" for the last update
            retrieved.setNumber_visits(retrieved.getNumber_visits() + 1);
            retrieved.setUpdated_at(Instant.now().plusSeconds(7200).toString());
            associatedUriRepository.save(retrieved);

            // adding the "sho.rt/" to the shortId to get the short uri
            retrieved.setShortId("sho.rt/" + retrieved.getShortId());
            String response = jsonResponse(200, retrieved);

            return ResponseEntity.ok().body(JsonFormatter.prettyPrint(response));
        } else {
            String error = jsonError(404, shortId);
            return ResponseEntity.status(404).body(JsonFormatter.prettyPrint(error));
        }
    }

    /**
     * Register an User
     **/
    public ResponseEntity<String> registerUser(User user){

        List<User> users = userRepository.findAll();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        user.setActive(true);

        if(users.isEmpty()){
            userRepository.save(user);
            return ResponseEntity.status(201).body(JsonFormatter.prettyPrint(
                    "{\"timestamp\":\"" + Instant.now().plusSeconds(7200) +"\"," +
                    "\"status\":\"201\"," +
                    "\"message\":\"User registered\"}"));
        } else if(users.stream().anyMatch(x -> x.getEmail().equals(user.getEmail()))) {
            return ResponseEntity.badRequest().body(JsonFormatter.prettyPrint(jsonError(400, null)));
        } else {
            userRepository.save(user);
            return ResponseEntity.status(201).body(JsonFormatter.prettyPrint(
                    "{\"timestamp\":\"" + Instant.now().plusSeconds(7200) +"\"," +
                            "\"status\":\"201\"," +
                            "\"message\":\"User registered\"}"));
        }
    }

    /**
     * Create a Json formatted string with the required datas
     **/
    private String jsonResponse(int status, AssociatedUri associatedUri){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return "{\"data\":" + gson.toJson(associatedUri) + "," +
                "\"_metadata\":{" +
                "\"status\":\"" + status + "\"," +
                "\"created_at\":\"" + associatedUri.getCreated_at() + "\"," +
                "\"updated_at\":\"" + associatedUri.getUpdated_at() + "\"}," +
                "\"_links\":{" +
                "\"self\":{\"href\":\"/api/v1/short_uris/" + associatedUri.getShortId().substring(7) + "\"}}";
    }

    /**
     * Create a Json formatted string for errors
     **/
    private String jsonError(int status, String shortId){

        switch(status){
            case 400:
                return "{\"timestamp\":\"" + Instant.now().plusSeconds(7200) +"\"," +
                        "\"status\":\"400\"," +
                        "\"error\":\"Bad Request\"," +
                        "\"message\":\"No message available\"}";
            case 404:
                return "{\"timestamp\":\"" + Instant.now().plusSeconds(7200) +"\"," +
                        "\"status\":\"404\"," +
                        "\"error\":\"Resource Not Found\"," +
                        "\"message\":\"No message available\"," +
                        "\"path\":\"/api/v1/short_uris/" + shortId + "\"}";
            default:
                return "";
        }
    }
}
