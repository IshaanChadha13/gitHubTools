package com.example.capstone.githubtools.repository;

import com.example.capstone.githubtools.model.AlertDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface AlertRepository extends ElasticsearchRepository<AlertDocument, String> {
    List<AlertDocument> findByRepoOwnerAndRepoName(String owner, String repo);

    List<AlertDocument> findByRepoOwnerAndRepoNameAndAlertType(String owner, String repo, String codeScanning);
}
