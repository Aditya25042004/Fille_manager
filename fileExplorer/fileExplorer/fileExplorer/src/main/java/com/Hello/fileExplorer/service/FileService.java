package com.Hello.fileExplorer.service;

import com.Hello.fileExplorer.config.ExplorerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class FileService {

    @Autowired
    private ExplorerConfig config;

    public Map<String, List<String>> listFilesAndFolders(String relativePath) throws IOException {
        Path folder = config.getRootPath().resolve(relativePath).normalize();

        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            throw new IOException("Folder does not exist.");
        }

        List<String> files = new ArrayList<>();
        List<String> folders = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    folders.add(entry.getFileName().toString());
                } else if (Files.isRegularFile(entry) && !entry.getFileName().toString().equals(".meta.json")) {
                    files.add(entry.getFileName().toString());
                }
            }
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("folders", folders);
        result.put("files", files);
        return result;
    }

    public void createFile(String relativePath, String name) throws IOException {
        Path filePath = config.getRootPath().resolve(relativePath).resolve(name).normalize();
        if (Files.exists(filePath)) {
            throw new IOException("File already exists.");
        }
        Files.createFile(filePath);
    }

    public void renameFile(String relativePath, String oldName, String newName) throws IOException {
        Path folder = config.getRootPath().resolve(relativePath).normalize();
        Path source = folder.resolve(oldName);
        Path target = folder.resolve(newName);

        if (!Files.exists(source) || Files.exists(target)) {
            throw new IOException("File rename error.");
        }

        Files.move(source, target);
    }

    public void deleteFile(String relativePath, String name) throws IOException {
        Path filePath = config.getRootPath().resolve(relativePath).resolve(name).normalize();
        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist.");
        }
        Files.delete(filePath);
    }

    public void uploadFile(String relativePath, MultipartFile file) throws IOException {
        Path folder = config.getRootPath().resolve(relativePath).normalize();
        if (!Files.exists(folder)) {
            throw new IOException("Destination folder does not exist.");
        }
        Path target = folder.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    public void createFolder(String parent, String name) throws IOException {
        Path folderPath = config.getRootPath().resolve(parent).resolve(name).normalize();

        if (Files.exists(folderPath)) {
            throw new IOException("Folder already exists.");
        }

        Files.createDirectories(folderPath);
    }

}
