package vn.edu.hcmute.grab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hcmute.grab.exception.FileInvalidException;
import vn.edu.hcmute.grab.exception.FileNotFoundException;
import vn.edu.hcmute.grab.exception.FileNotSupportException;
import vn.edu.hcmute.grab.exception.FileStorageException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path fileInputStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {

        this.fileInputStorageLocation = Paths.get("../".concat(fileStorageProperties.getUploadDir())).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileInputStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFileImage(MultipartFile file) {
        String fileName = UUID.randomUUID().toString().concat(".png");

        try {
            // Copy file to the target location (Replacing existing file with the same name)
            storeMultipartFile(fileInputStorageLocation, fileName, file);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException(String.format("Could not store file %s. Please try again!", fileName), ex);
        }
    }


    public Resource loadFileAsResource(URI fileUri) {
        try {
            Resource resource = new UrlResource(fileUri);
            if (!resource.exists())
                throw new FileNotFoundException("File not found ");
            return resource;
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found ");
        }
    }


    public File restoreFileImage(String fileName) {
        File file = this.fileInputStorageLocation.resolve(fileName).toFile();

        if (file.isFile()) {
            return file;
        } else {
            throw new FileNotFoundException(String.format("File not found %s", fileName));
        }
    }

    private void storeMultipartFile(Path location, String fileName, MultipartFile file) throws IOException {

        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = location.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        log.info("Store file success " + targetLocation.toString());
    }


    public void validationFile(MultipartFile file, MediaType mediaType) {
        if (!mediaType.toString().equals(file.getContentType())) {
            throw new FileNotSupportException("Format file is not " + mediaType.getSubtype());
        }

        if (file.getSize() == 0l) {
            throw new FileInvalidException("File is empty");
        }

        if (file.getOriginalFilename().contains("..")) {
            throw new FileStorageException(String.format("Sorry! Filename contains invalid path sequence %s", file.getOriginalFilename()));
        }
    }

}
