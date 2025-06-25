package com.Hello.fileExplorer.controller;

import com.Hello.fileExplorer.service.FileService;
import com.Hello.fileExplorer.service.FolderMetadata;
import com.Hello.fileExplorer.service.MetadataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private MetadataManager metadataManager;

    @GetMapping("/list")
    public Map<String, Object> listFolder(@RequestParam String path) throws IOException {
        Map<String, List<String>> data = fileService.listFilesAndFolders(path);

        // Apply metadata sorting for folders
        List<String> folders = data.get("folders");
        FolderMetadata metadata = metadataManager.getMetadata(path.replace("\\", "/"));
        if (metadata != null && metadata.getSubFolders() != null) {
            List<String> sorted = metadata.getSubFolders();
            folders.sort(Comparator.comparingInt(sorted::indexOf));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("folders", folders);
        result.put("files", data.get("files"));
        return result;
    }

    @PostMapping("/create")
    public String createFile(@RequestParam String path, @RequestParam String name) throws IOException {
        fileService.createFile(path, name);
        return "File created successfully.";
    }

    @PostMapping("/rename")
    public String renameFile(@RequestParam String path,
                             @RequestParam String oldName,
                             @RequestParam String newName) throws IOException {
        fileService.renameFile(path, oldName, newName);
        return "File renamed successfully.";
    }

    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam String path, @RequestParam String name) throws IOException {
        fileService.deleteFile(path, name);
        return "File deleted successfully.";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam String path, @RequestParam MultipartFile file) throws IOException {
        fileService.uploadFile(path, file);
        return "File uploaded successfully.";
    }

    @PostMapping("/folder/create")
    public ResponseEntity<String> createFolder(@RequestParam String parent,
                                               @RequestParam String name) {
        try {
            fileService.createFolder(parent, name);
            metadataManager.addFolder(parent.replace("\\", "/"), name);
            return ResponseEntity.ok("Folder created successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
