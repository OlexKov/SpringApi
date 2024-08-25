package org.example.services;

import org.example.exceptions.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.example.interfaces.IStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class StorangeService implements IStorageService {
    private final Path filesDirPath;
    private final String filesDir = "target/files";
    public  StorangeService() throws IOException {
        filesDirPath = Paths.get(filesDir);
        if(!Files.exists(filesDirPath)){
            try {
                Files.createDirectory(filesDirPath);
            }
            catch (IOException e) {
                throw new StorageException("Could not initialize storage", e);
            }

        }
    }
    @Override
    public String saveFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            String originalName = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).toString();
            String extension  =  FilenameUtils.getExtension(originalName);
            String fileName = java.util.UUID.randomUUID().toString() + "." + extension;

            Path destinationFile = this.filesDirPath.resolve(fileName)
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.filesDirPath.toAbsolutePath())) {
                  throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }

    }

    @Override
    public void deleteFile(String fileName) {
        if(fileName != null && !fileName.isEmpty()){
            Path filePath = filesDirPath.resolve(fileName);
            try {
                Files.deleteIfExists(filePath);
            }
            catch (IOException e) {

            }
        }
        else throw new StorageException("File name not be empty");
    }

    @Override
    public File getFile(String fileName) throws IOException {
         return    new File(filesDir + "/" + fileName);
    }
}
