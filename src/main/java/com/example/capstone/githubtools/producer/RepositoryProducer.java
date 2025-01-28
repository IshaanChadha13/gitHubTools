package com.example.capstone.githubtools.producer;

import com.example.capstone.githubtools.dto.RepoMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RepositoryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.repo-schedule-topic}")
    private String repoScheduleTopic;

    public RepositoryProducer(KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendRepoMessage(String owner, String repository) {
        try {
            RepoMessage repoMsg = new RepoMessage(owner, repository);
            String jsonString = objectMapper.writeValueAsString(repoMsg);

            System.out.println("Sent kafka message to consumer");
            kafkaTemplate.send(repoScheduleTopic, jsonString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
