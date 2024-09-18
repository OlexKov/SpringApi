package org.example.services;

import net.coobird.thumbnailator.Thumbnails;
import org.example.configurations.StorageProperties;
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


@Service
public class StorageService implements IStorageService {
    private final int [] sizes = {32,150,300,600,1200};
    private final Path rootLocation;

    public StorageService(StorageProperties properties) throws IOException {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() throws IOException {
        if(!Files.exists(rootLocation))
            Files.createDirectory(rootLocation);
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

            Path destinationFile = this.rootLocation.resolve(fileName)
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
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
            Path filePath = rootLocation.resolve(fileName);
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
            String fileSave = rootLocation +"/"+size+"_"+randomFileName;
            Thumbnails.of(bufferedImage).size(size, size).outputFormat(ext).toFile(fileSave);
        }
        return randomFileName;
    }

    @Override
    public void deleteImage(String imageName) throws IOException {
        if(imageName != null && !imageName.isEmpty()){
            for(int size:sizes){
                String name = size + "_" + imageName;
                Path imagePath = rootLocation.resolve(name);
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
