package com.example.capstone.githubtools.service;

import com.example.capstone.githubtools.githubclient.GithubApiClient;
import com.example.capstone.githubtools.model.AlertDocument;
import com.example.capstone.githubtools.repository.RepoTokenMappingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ToolSchedulerService {

    private final RepoTokenMappingRepository tokenMappingRepo;
    private final GithubApiClient githubApiClient;
    private final AlertIndexer alertIndexer;

    public ToolSchedulerService(
            RepoTokenMappingRepository tokenMappingRepo,
            GithubApiClient githubApiClient,
            AlertIndexer alertIndexer
    ) {
        this.tokenMappingRepo = tokenMappingRepo;
        this.githubApiClient = githubApiClient;
        this.alertIndexer = alertIndexer;
    }

    public void processRepository(String owner, String repo) {
        // 1) Fetch the token from DB
        var mappingOpt = tokenMappingRepo.findByOwnerAndRepository(owner, repo);
        if (mappingOpt.isEmpty()) {
            System.out.println("No PAT found for " + owner + "/" + repo);
            return;
        }

        String pat = mappingOpt.get().getPersonalAccessToken();

        // 2) Fetch data from GitHub
        var codeAlerts = githubApiClient.fetchCodeScanningAlerts(owner, repo, pat);
        var dependabotAlerts = githubApiClient.fetchDependabotAlerts(owner, repo, pat);
        var secretAlerts = githubApiClient.fetchSecretScanningAlerts(owner, repo, pat);

        // 3) Transform into AlertDocument
        List<AlertDocument> allAlerts = new ArrayList<>();
        allAlerts.addAll(transformAlerts(codeAlerts, "CODE_SCANNING", owner, repo));
        allAlerts.addAll(transformAlerts(dependabotAlerts, "DEPENDABOT", owner, repo));
        allAlerts.addAll(transformAlerts(secretAlerts, "SECRET_SCANNING", owner, repo));

        if (allAlerts.isEmpty()) {
            System.out.println("No alerts for " + owner + "/" + repo);
            return;
        }

        // 4) Index in Elasticsearch
        alertIndexer.indexAlerts(allAlerts);
        System.out.println("Indexed " + allAlerts.size() + " alerts for " + owner + "/" + repo);
    }

    private List<AlertDocument> transformAlerts(List<Map<String, Object>> rawAlerts,
                                                String alertType,
                                                String owner,
                                                String repo) {
        List<AlertDocument> result = new ArrayList<>();
        for (Map<String, Object> raw : rawAlerts) {
            AlertDocument doc = new AlertDocument();
            doc.setRepoOwner(owner);
            doc.setRepoName(repo);
            doc.setAlertType(alertType);

            // some alerts have "number" or "id" in the JSON
            Object number = raw.get("number");
            doc.setAlertId(number != null ? number.toString() : null);

            // example of reading "state" or "severity"
            if (raw.containsKey("state")) {
                doc.setState(raw.get("state").toString());
            }
            if (raw.containsKey("severity")) {
                doc.setSeverity(raw.get("severity").toString());
            }

            doc.setMetadata(raw); // store full object in metadata if desired
            result.add(doc);
        }
        return result;
    }
}
