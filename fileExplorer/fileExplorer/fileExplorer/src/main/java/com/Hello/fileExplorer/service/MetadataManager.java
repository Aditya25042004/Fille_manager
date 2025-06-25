package com.Hello.fileExplorer.service;

import com.Hello.fileExplorer.config.ExplorerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
public class MetadataManager {

    private final Path rootPath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MetadataManager(ExplorerConfig config) {
        this.rootPath = config.getRootPath();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private Path getMetadataFile(Path folderPath) {
        return folderPath.resolve(".metadata.json");
    }

    // Load metadata or return default
    public FolderMetadata getMetadata(String relativePath) {
        Path folder = rootPath.resolve(relativePath).normalize();
        Path metaFile = getMetadataFile(folder);

        if (Files.exists(metaFile)) {
            try {
                return objectMapper.readValue(metaFile.toFile(), FolderMetadata.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FolderMetadata defaultMetadata = new FolderMetadata();
        defaultMetadata.setCreated(Instant.now());
        defaultMetadata.setModified(Instant.now());
        defaultMetadata.setSortBy("name"); // default
        defaultMetadata.setSubFolders(new ArrayList<>());
        return defaultMetadata;
    }

    // Save metadata
    public void saveMetadata(String relativePath, FolderMetadata metadata) {
        Path folder = rootPath.resolve(relativePath).normalize();
        Path metaFile = getMetadataFile(folder);
        try {
            objectMapper.writeValue(metaFile.toFile(), metadata);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a subfolder entry to metadata
    public void addFolder(String parentRelativePath, String folderName) {
        FolderMetadata metadata = getMetadata(parentRelativePath);
        metadata.setModified(Instant.now());
        if (metadata.getSubFolders() == null) {
            metadata.setSubFolders(new ArrayList<>());
        }
        if (!metadata.getSubFolders().contains(folderName)) {
            metadata.getSubFolders().add(folderName);
        }
        saveMetadata(parentRelativePath, metadata);

        // Create metadata for new folder
        String childPath = Paths.get(parentRelativePath, folderName).toString().replace("\\", "/");
        FolderMetadata childMetadata = new FolderMetadata();
        childMetadata.setCreated(Instant.now());
        childMetadata.setModified(Instant.now());
        childMetadata.setSortBy("name");
        childMetadata.setSubFolders(new ArrayList<>());
        saveMetadata(childPath, childMetadata);
    }

    // Get subfolders
    public List<String> getSubFolders(String relativePath) {
        FolderMetadata metadata = getMetadata(relativePath);
        return metadata.getSubFolders() != null ? metadata.getSubFolders() : new ArrayList<>();
    }

    // Update sort option for a folder
    public void updateSort(String relativePath, String sortBy) {
        FolderMetadata metadata = getMetadata(relativePath);
        metadata.setSortBy(sortBy);
        metadata.setModified(Instant.now());
        saveMetadata(relativePath, metadata);
    }
}
