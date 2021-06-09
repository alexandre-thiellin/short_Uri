package com.example.shorturi.repository;

import com.example.shorturi.model.AssociatedUri;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Project ShortUri
 *
 * @author Alexandre
 **/
public interface AssociatedUriRepository extends JpaRepository<AssociatedUri, Long> {

    Optional<AssociatedUri> findByShortId(String shortUri);
    Optional<AssociatedUri> findByLongUri(String longUri);
    boolean existsByShortId(String shortUri);
    boolean existsByLongUri(String longUri);
}
