package org.moparforia.shared.tracks.util;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemExtension implements BeforeEachCallback, AfterEachCallback {
    private static final String[] DIRS = {
            "/tracks/tracks",
            "/tracks/sets",
    };

    private FileSystem fileSystem;
    private final String rootDir;
    private final String[] createDirs;

    public FileSystemExtension(String rootDir, String[] dirs)
    {
        this.createDirs = dirs;
        this.rootDir = rootDir;
    }

    public FileSystemExtension(String rootDir) {
        this(rootDir, DIRS);
    }

    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    public void copyFile(String src) throws IOException, URISyntaxException {
        Path srcPath = getRootDir().resolve(src);
        Path targetPath = fileSystem.getPath(src);
        Files.copy(srcPath, targetPath);
    }

    /**
     * Copies directory from resource folder into InMemory file system
     * @param dir Path to the dir
     */
    public void copyDir(String dir) throws IOException, URISyntaxException {
        Path base = getRootDir();
        Path resource = base.resolve(dir);
        List<Path> files = Files.walk(resource)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        for (Path file : files) {
            Path relative_path = fileSystem.getPath(base.relativize(file).toString().replace(FileSystems.getDefault().getSeparator(), fileSystem.getSeparator()));
            Files.copy(base.resolve(file), relative_path);
        }
    }

    public void copyAll() throws IOException, URISyntaxException {
        copyDir("tracks/");
    }

    private Path getRootDir() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource(rootDir).toURI());
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (this.fileSystem != null) {
            this.fileSystem.close();
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        this.fileSystem = MemoryFileSystemBuilder.newEmpty().build("test");
        for (String dir : createDirs) {
            Path path = fileSystem.getPath(dir);
            Files.createDirectories(path);
        }
    }
}
