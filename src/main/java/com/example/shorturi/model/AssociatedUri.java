package com.example.shorturi.model;

import javax.persistence.*;

/**
 * Project ShortUri
 *
 * @author Alexandre
 * This class describe a pair of uri with their metadatas
 **/
@Entity
@Table(name = "associated_uri")
public class AssociatedUri {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private long user_id;

    @Column
    private String shortId;

    @Column
    private String longUri;

    @Column
    private int number_visits;

    @Column
    private String created_at;

    @Column
    private String updated_at;

    public AssociatedUri() {
    }

    public AssociatedUri(long id, long user_id, String shortId, String longUri, int number_visits, String created_at, String updated_at) {
        this.id = id;
        this.user_id = user_id;
        this.shortId = shortId;
        this.longUri = longUri;
        this.number_visits = number_visits;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortUri) {
        this.shortId = shortUri;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
