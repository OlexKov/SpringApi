package org.example.interfaces;

import org.example.models.FileFormats;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStorageService {
    String saveFile(MultipartFile file);
    void deleteFile(String fileName);
    String saveImage(MultipartFile file, FileFormats format) throws IOException;
    void deleteImage(String imageName) throws IOException;
}
