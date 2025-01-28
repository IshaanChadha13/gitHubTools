package com.example.capstone.githubtools.model;

import jakarta.persistence.*;

@Entity
@Table(name="repo_token_mappings")
public class RepoTokenMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;
    private String repository;
    private String personalAccessToken;

    public RepoTokenMapping() {}

    public RepoTokenMapping(String owner, String repository, String personalAccessToken) {
        this.owner = owner;
        this.repository = repository;
        this.personalAccessToken = personalAccessToken;
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
