package com.example.capstone.githubtools.consumer;

import com.example.capstone.githubtools.dto.RepoMessage;
import com.example.capstone.githubtools.service.ToolSchedulerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RepositoryConsumer {

    private final ToolSchedulerService toolSchedulerService;
    private final ObjectMapper objectMapper;

    public RepositoryConsumer(ToolSchedulerService toolSchedulerService, ObjectMapper objectMapper) {
        this.toolSchedulerService = toolSchedulerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${kafka.topics.repo-schedule-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(String message) {
        try {
            RepoMessage repoMsg = objectMapper.readValue(message, RepoMessage.class);
            String owner = repoMsg.getOwner();
            String repository = repoMsg.getRepository();

            System.out.println("Consumer received: " + owner + "/" + repository);

            // calls the service to fetch GitHub alerts and index them in Elasticsearch
            toolSchedulerService.processRepository(owner, repository);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
