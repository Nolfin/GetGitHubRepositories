package com.example.test.atipera.demo.dto;

import java.util.List;

public record RepositoryDTO(String repoName, String ownerLogin, List<BranchDTO> branches) {

}