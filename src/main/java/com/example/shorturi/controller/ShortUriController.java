package com.example.shorturi.controller;

import com.example.shorturi.model.AssociatedUri;
import com.example.shorturi.model.ShortUriManager;
import com.example.shorturi.repository.AssociatedUriRepository;
import com.jayway.jsonpath.internal.JsonFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project ShortUri
 *
 * @author Alexandre
 **/
@RestController
public class ShortUriController {

    private ShortUriManager shortUriManager;
    private final AssociatedUriRepository associatedUriRepository;

    public ShortUriController(AssociatedUriRepository associatedUriRepository){
        this.associatedUriRepository = associatedUriRepository;
        shortUriManager = ShortUriManager.getInstance();
    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> postAssociatedUri(@RequestBody AssociatedUri associatedUri){

        if(associatedUri.getLongUri() == null){
            return ResponseEntity.status(400).body(JsonFormatter.prettyPrint("{\"error\":\"Bad request - wrong format\"}"));
        }

        if(!associatedUriRepository.existsByLongUri(associatedUri.getLongUri())){

            String shortUri = shortUriManager.createShortUri(associatedUri.getLongUri());
            associatedUri.setShortUri(shortUri);
            associatedUri.setNumber_visits(0);

            associatedUriRepository.save(associatedUri);
            return ResponseEntity.status(201).body(JsonFormatter.prettyPrint(associatedUri.getShortUri()));
        } else {
            associatedUri = associatedUriRepository.findByLongUri(associatedUri.getLongUri()).get();
            return ResponseEntity.ok().body(JsonFormatter.prettyPrint(associatedUri.getShortUri()));
        }
    }

    @GetMapping(path = "/redirect")
    public ResponseEntity<String> getLongUri(@RequestBody AssociatedUri associatedUri){

        if(associatedUri.getShortUri() == null){
            return ResponseEntity.status(400).body(JsonFormatter.prettyPrint("{\"error\" : \"Bad request - wrong format of the request\"}"));
        } else if(associatedUri.getShortUri().length() != 15){
            return ResponseEntity.status(400).body(JsonFormatter.prettyPrint("{\"error\" : \"Bad request - wrong length of the uri\"," +
                    "\"expected length\":\"15\"," +
                    "\"actual length\":\""+associatedUri.getShortUri().length()+"\"}"));
        } else if(!associatedUri.getShortUri().startsWith("sho.rt/")){
            return ResponseEntity.status(400).body(JsonFormatter.prettyPrint("{\"error\" : \"Bad request - wrong format of the uri\"," +
                    "\"expected format\":\"sho.rt/\"," +
                    "\"actual format\":\""+associatedUri.getShortUri().substring(0,7)+"\"}"));
        }

        AssociatedUri retrieved = associatedUriRepository.findByShortUri(associatedUri.getShortUri()).orElse(null);

        if(retrieved != null){
            retrieved.setNumber_visits(retrieved.getNumber_visits() + 1);
            associatedUriRepository.save(retrieved);
            return ResponseEntity.ok().body(retrieved.getLongUri());
        } else {
            return ResponseEntity.status(404).body(JsonFormatter.prettyPrint("{\"error\" : \"ressource not found\"}"));
        }
    }
}
