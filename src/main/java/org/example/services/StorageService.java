package org.example.services;

import net.coobird.thumbnailator.Thumbnails;
import org.example.exceptions.StorageException;
import org.apache.commons.io.FilenameUtils;
import org.example.interfaces.IStorageService;
import org.example.models.FileFormats;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class StorageService implements IStorageService {
    private final Path filesDirPath,imageDirPath;
    private final String filesDir = "src/main/resources/public/files";
    private final String imagesDir = "src/main/resources/public/images";
    private final int [] sizes = {32,150,300,600,1200};
    public StorageService() throws IOException {
        filesDirPath = Paths.get(filesDir);
        imageDirPath = Paths.get(imagesDir);
        if(!Files.exists(filesDirPath)){
            try {
                Files.createDirectories(filesDirPath);
            }
            catch (IOException e) {
                throw new StorageException("Could not initialize storage", e);
            }

        }
        if(!Files.exists(imageDirPath)){
            try {
                Files.createDirectories(imageDirPath);
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
            catch (IOException ignored) { }
        }
        else throw new StorageException("File name not be empty");
    }

    @Override
    public String saveImage(MultipartFile file, FileFormats format) throws IOException {
        var bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        return saveBufferedImage(bufferedImage,format);
    }

    @Override
    public String saveImage(String fileUrl, FileFormats format) throws IOException {
         try (InputStream inputStream = new URL(fileUrl).openStream()) {
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                return saveBufferedImage(bufferedImage, format);
         }
    }

    @Override
    public String saveBufferedImage(BufferedImage bufferedImage, FileFormats format) throws IOException {
        String ext = format.name().toLowerCase();
        String randomFileName = UUID.randomUUID().toString()+"."+ext;
        for (var size : sizes) {
            String fileSave = imagesDir +"/"+size+"_"+randomFileName;
            Thumbnails.of(bufferedImage).size(size, size).outputFormat(ext).toFile(fileSave);
        }
        return randomFileName;
    }

    @Override
    public void deleteImage(String imageName) throws IOException {
        if(imageName != null && !imageName.isEmpty()){
            for(int size:sizes){
                String name = size + "_" + imageName;
                Path imagePath = imageDirPath.resolve(name);
                try {
                    Files.deleteIfExists(imagePath);
                }
                catch (IOException ignored) {}
            }
        }
        else throw new StorageException("File name not be empty");
    }

    @Override
    public void deleteImages(Iterable<String> imageNames) throws IOException {
        for(String name:imageNames){
            deleteImage(name);
        }
    }
}
