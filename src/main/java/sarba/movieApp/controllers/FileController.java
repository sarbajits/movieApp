package sarba.movieApp.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sarba.movieApp.service.FileService;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${project.fileLocation}")
    private String path;

    @PostMapping("/upload")
    private ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) throws IOException{
        String uploadedFileName= fileService.uploadFile(path,file);

        return ResponseEntity.ok("File uploaded with name of "+uploadedFileName);
    }


    @GetMapping("/{fileName}")
    private void serveFile(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourceFile=fileService.getFile(path,fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile,response.getOutputStream());
    }
}
