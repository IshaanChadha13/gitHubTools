package com.example.capstone.githubtools;

import com.example.capstone.githubtools.producer.RepositoryProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestProducerRunner implements CommandLineRunner {

    private final RepositoryProducer repositoryProducer;

    public TestProducerRunner(RepositoryProducer repositoryProducer) {
        this.repositoryProducer = repositoryProducer;
    }

    @Override
    public void run(String... args) {
        // Send a test message when app starts
        repositoryProducer.sendRepoMessage("IshaanChadha13", "juice-shop");
        System.out.println("Sent a test message to Kafka!");
    }
}
