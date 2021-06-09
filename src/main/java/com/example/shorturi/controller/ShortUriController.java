package com.example.shorturi.controller;

import com.example.shorturi.model.AssociatedUri;
import com.example.shorturi.model.User;
import com.example.shorturi.repository.AssociatedUriRepository;
import com.example.shorturi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Project ShortUri
 *
 * @author Alexandre
 **/
@RestController
public class ShortUriController {

    private final ShortUriManager shortUriManager;

    public ShortUriController(AssociatedUriRepository associatedUriRepository, UserRepository userRepository){
        shortUriManager = ShortUriManager.getInstance(associatedUriRepository, userRepository);
    }

    @PostMapping(path = "/short_uris", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createAssociatedUri(@RequestBody AssociatedUri associatedUri, Authentication authentication){

        return shortUriManager.createAssociatedUri(associatedUri, authentication);
    }

    @GetMapping(path = "/short_uris/{shortId}", produces = "application/json")
    public ResponseEntity<String> getAssociationUri(@PathVariable String shortId){

        return shortUriManager.getAssociatedUri(shortId);
    }

    @PostMapping(path = "/register", produces = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody User user){

        return shortUriManager.registerUser(user);
    }
}
