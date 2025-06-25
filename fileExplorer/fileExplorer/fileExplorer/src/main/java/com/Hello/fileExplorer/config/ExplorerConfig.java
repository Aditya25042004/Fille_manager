package com.Hello.fileExplorer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ExplorerConfig {

    @Value("${explorer.root.path}")
    private String rootPath;

    public Path getRootPath() {
        return Paths.get(rootPath).toAbsolutePath().normalize();
    }

}
