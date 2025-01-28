package com.example.capstone.githubtools.controller;

import com.example.capstone.githubtools.model.AlertDocument;
import com.example.capstone.githubtools.repository.AlertRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping("/alerts")
    public List<AlertDocument> getAllAlerts() {
        Iterable<AlertDocument> iterable = alertRepository.findAll();
        List<AlertDocument> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @GetMapping("/{owner}/{repo}")
    public List<AlertDocument> getAlertsByOwnerAndRepo(
            @PathVariable String owner,
            @PathVariable String repo) {

        return alertRepository.findByRepoOwnerAndRepoName(owner, repo);
    }

    @GetMapping("/{owner}/{repo}/code-scanning")
    public List<AlertDocument> getCodeScanningAlerts(@PathVariable String owner,
                                                     @PathVariable String repo) {
        return alertRepository.findByRepoOwnerAndRepoNameAndAlertType(owner, repo, "CODE_SCANNING");
    }

    @GetMapping("/{owner}/{repo}/dependabot")
    public List<AlertDocument> getDependabotAlerts(@PathVariable String owner,
                                                     @PathVariable String repo) {
        return alertRepository.findByRepoOwnerAndRepoNameAndAlertType(owner, repo, "DEPENDABOT");
    }

    @GetMapping("/{owner}/{repo}/secret-scanning")
    public List<AlertDocument> getSecretScanningAlerts(@PathVariable String owner,
                                                     @PathVariable String repo) {
        return alertRepository.findByRepoOwnerAndRepoNameAndAlertType(owner, repo, "SECRET_SCANNING");
    }

}
