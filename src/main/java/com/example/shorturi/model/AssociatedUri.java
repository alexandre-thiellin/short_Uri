package com.example.shorturi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Project ShortUri
 *
 * @author Alexandre
 * Cette classe decrit un objet avec 3 champs, une url courte, une url longue
 * et un nombre de visites associé.
 **/
@Entity
@Table(name = "short_uris")
public class AssociatedUri {

    @Id
    private long id;

    @Column
    private String shortUri;

    @Column
    private String longUri;

    @Column
    private int number_visits;

    public AssociatedUri() {

    }

    public AssociatedUri(String shortUri, String longUri, int number_visits) {
        this.shortUri = shortUri;
        this.longUri = longUri;
        this.number_visits = number_visits;
    }

    public String getShortUri() {
        return shortUri;
    }

    public void setShortUri(String shortUri) {
        this.shortUri = shortUri;
    }

    public String getLongUri() {
        return longUri;
    }

    public void setLongUri(String longUri) {
        this.longUri = longUri;
    }

    public int getNumber_visits() {
        return number_visits;
    }

    public void setNumber_visits(int number_visits) {
        this.number_visits = number_visits;
    }
}
