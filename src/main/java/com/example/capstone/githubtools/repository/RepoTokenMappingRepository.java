package com.example.capstone.githubtools.repository;

import com.example.capstone.githubtools.model.RepoTokenMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoTokenMappingRepository extends JpaRepository<RepoTokenMapping, Long> {
    Optional<RepoTokenMapping> findByOwnerAndRepository(String owner, String repository);
}
