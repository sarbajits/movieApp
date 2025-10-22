package sarba.movieApp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarba.movieApp.exceptions.FileNotAvailableException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {

        String formattedDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss"));

        String originalFileName = file.getOriginalFilename();
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        String formattedBaseName = baseName.replaceAll("\\s+", "_"); // replaces spaces with underscores

        String fileName = formattedBaseName + "-" + formattedDateTime + extension;


        String filePath = path + File.separator + fileName;

        File f = new File(path);

        if (!f.exists()) {
            f.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    public InputStream getFile(String path, String fileName) throws FileNotAvailableException {
        String filePath = path + File.separator + fileName;

        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new FileNotAvailableException("File not found at path: " + filePath);
        }
    }

}
