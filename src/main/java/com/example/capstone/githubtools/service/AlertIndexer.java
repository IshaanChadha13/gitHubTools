package com.example.capstone.githubtools.service;

import com.example.capstone.githubtools.model.AlertDocument;
import com.example.capstone.githubtools.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertIndexer {

    private final AlertRepository alertRepository;

    public AlertIndexer(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void indexAlerts(List<AlertDocument> alerts) {
        alertRepository.saveAll(alerts);
    }
}
