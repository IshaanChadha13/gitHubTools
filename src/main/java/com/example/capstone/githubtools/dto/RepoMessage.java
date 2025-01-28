package com.example.capstone.githubtools.dto;

public class RepoMessage {

    private String owner;
    private String repository;

    // Constructors
    public RepoMessage() {}
    public RepoMessage(String owner, String repository) {
        this.owner = owner;
        this.repository = repository;
    }

    // Getters and setters
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
}
