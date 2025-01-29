package com.example.capstone.githubtools.service;

import com.example.capstone.githubtools.githubclient.GithubApiClient;
import com.example.capstone.githubtools.repository.RepoTokenMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ToolSchedulerService {

    private final RepoTokenMappingRepository tokenMappingRepo;
    private final GithubApiClient githubApiClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.parser-topic}")
    private String parserTopic;

    @Value("${myapp.local-storage}")
    private String localStoragePath;

    public ToolSchedulerService(
            RepoTokenMappingRepository tokenMappingRepo,
            GithubApiClient githubApiClient,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.tokenMappingRepo = tokenMappingRepo;
        this.githubApiClient = githubApiClient;
        this.kafkaTemplate = kafkaTemplate;
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
        String ownerRepoFolder = localStoragePath + File.separator + owner + "-" + repo;
        File baseDir = new File(ownerRepoFolder);
        if (!baseDir.exists()) {
            baseDir.mkdirs(); // create if not exists
        }

        // 4) Write each tool’s alerts into its own subfolder
        writeAndSendToParser(owner, repo, "CODE_SCANNING", codeAlerts);
        writeAndSendToParser(owner, repo, "DEPENDABOT", dependabotAlerts);
        writeAndSendToParser(owner, repo, "SECRET_SCANNING", secretAlerts);
    }

    private void writeAndSendToParser(String owner, String repo, String toolType,
                                      List<Map<String,Object>> alerts) {
        // subfolder => e.g. "/tmp/alerts/IshaanChadha13-juice-shop/CODE_SCANNING"
        String subFolderPath = localStoragePath
                + File.separator + owner + "-" + repo
                + File.separator + toolType;
        File subFolder = new File(subFolderPath);
        if (!subFolder.exists()) {
            subFolder.mkdirs();
        }

        // file name => e.g. "IshaanChadha13-juice-shop-CODE_SCANNING-<timestamp>.json"
        String fileName = owner + "-" + repo + "-" + toolType
                + "-" + System.currentTimeMillis() + ".json";
        File outFile = new File(subFolder, fileName);

        // Write the alerts to that file
        writeToJsonFile(alerts, outFile);

        // Publish the file path to parser topic
        String filePath = outFile.getAbsolutePath();
        kafkaTemplate.send(parserTopic, filePath);
        System.out.println("Saved " + toolType + " alerts to: " + filePath);
    }

    private void writeToJsonFile(Object data, File file) {
        ObjectMapper mapper = new ObjectMapper();
        try (FileWriter fw = new FileWriter(file)) {
            mapper.writeValue(fw, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private List<AlertDocument> transformAlerts(List<Map<String, Object>> rawAlerts,
//                                                String alertType,
//                                                String owner,
//                                                String repo) {
//        List<AlertDocument> result = new ArrayList<>();
//        for (Map<String, Object> raw : rawAlerts) {
//            AlertDocument doc = new AlertDocument();
//            doc.setRepoOwner(owner);
//            doc.setRepoName(repo);
//            doc.setAlertType(alertType);
//
//            // some alerts have "number" or "id" in the JSON
//            Object number = raw.get("number");
//            doc.setAlertId(number != null ? number.toString() : null);
//
//            // example of reading "state" or "severity"
//            if (raw.containsKey("state")) {
//                doc.setState(raw.get("state").toString());
//            }
//            if (raw.containsKey("severity")) {
//                doc.setSeverity(raw.get("severity").toString());
//            }
//
//            doc.setMetadata(raw); // store full object in metadata if desired
//            result.add(doc);
//        }
//        return result;
//    }
}
