package com.example.capstone.githubtools.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.repo-schedule-topic}")
    private String repoScheduleTopic;

    @Value("${kafka.topics.partition-count}")
    private int partitionCount;

    @Value("${kafka.topics.replication-factor}")
    private short replicationFactor;

    @Bean
    public NewTopic repoScheduleTopic() {
        return TopicBuilder.name(repoScheduleTopic)
                .partitions(partitionCount)
                .replicas(replicationFactor)
                .build();
    }
}
