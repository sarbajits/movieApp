package sarba.movieApp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sarba.movieApp.dto.MailBody;
import sarba.movieApp.dto.MovieDto;
import sarba.movieApp.dto.MoviePageResponse;
import sarba.movieApp.service.EmailService;
import sarba.movieApp.service.MovieService;
import sarba.movieApp.utils.AppConstants;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie/")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/debug")
    public String debugAuth(Authentication auth) {
        return auth.getAuthorities().toString();
    }

    @GetMapping("/mail")
    public void testMail(@RequestBody MailBody mailBody){
        emailService.sendSimpleMail(mailBody);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<MovieDto> addMovie(@RequestPart MultipartFile file, @RequestPart String movieDtoObj) throws IOException {

        MovieDto movieDto = convertToDto(movieDtoObj);

        return new ResponseEntity<>(movieService.addMovie(movieDto, file), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/edit")
    public ResponseEntity<MovieDto> editMovie(@RequestPart MultipartFile file, @RequestPart String movieDtoObj) throws IOException {

        MovieDto movieDto = convertToDto(movieDtoObj);

        return new ResponseEntity<>(movieService.addMovie(movieDto, file), HttpStatus.CREATED);
    }

    private MovieDto convertToDto(String obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(obj, MovieDto.class);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieById(@PathVariable Integer id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/delete/{id}")
    public boolean deleteMovieById(@PathVariable Integer id) {
        return movieService.deleteMovieById(id);
    }

    @GetMapping("/all-old")
    public List<MovieDto> getALlMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/allByPage")
    public ResponseEntity<MoviePageResponse> getALlMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }

    @GetMapping("/all")
    public ResponseEntity<MoviePageResponse> getALlMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,sortOrder));
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateMoviesPdf(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder,
            @RequestParam(required = false) String email
    ) throws IOException, MessagingException {

        MoviePageResponse movieResponse = movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, sortOrder);
        List<MovieDto> movies = movieResponse.MovieDtos();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter writer = PdfWriter.getInstance(document, out);

        byte[] userPassword = "user".getBytes();
        byte[] ownerPassword = "owner".getBytes();

        writer.setEncryption(
                userPassword,
                ownerPassword,
                PdfWriter.ALLOW_DEGRADED_PRINTING, // Set permissions
                PdfWriter.ENCRYPTION_AES_128 // Or another constant like STANDARD_ENCRYPTION_128
        );

        document.open();

        // Load your image (can be from classpath, URL, or file)
        Image watermark = Image.getInstance("src/main/resources/static/spring-boot.png");

        // Set transparency (optional)
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.15f); // 15% opacity

        // Get the direct content under text
        PdfContentByte canvas = writer.getDirectContentUnder();

        // Scale image to fit page width/height
        watermark.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());

        // Position image (centered)
        float x = (PageSize.A4.getWidth() - watermark.getScaledWidth()) / 2;
        float y = (PageSize.A4.getHeight() - watermark.getScaledHeight()) / 2;
        watermark.setAbsolutePosition(x, y);

        // Apply transparency
        canvas.saveState();
        canvas.setGState(gs);

        // Add image under text
        canvas.addImage(watermark);
        canvas.restoreState();

        // Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Movies List (Page " + movieResponse.pageNumber() + " of " + movieResponse.totalPages() + ")", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Table
        PdfPTable table = new PdfPTable(6); // 6 columns
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 2, 2, 3, 1.5f});

        // Table header
        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        addTableHeader(table, headerFont, "ID", "Title", "Director", "Studio", "Cast", "Year");

        // Table data
        Font dataFont = new Font(Font.HELVETICA, 11);
        for (MovieDto movie : movies) {
            table.addCell(new Phrase(String.valueOf(movie.getMovieId()), dataFont));
            table.addCell(new Phrase(movie.getTitle(), dataFont));
            table.addCell(new Phrase(movie.getDirector(), dataFont));
            table.addCell(new Phrase(movie.getStudio(), dataFont));
            table.addCell(new Phrase(String.join(", ", movie.getMovieCast()), dataFont));
            table.addCell(new Phrase(String.valueOf(movie.getReleaseYear()), dataFont));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph(
                "Total Movies: " + movieResponse.totalElements() +
                        " | Page: " + movieResponse.pageNumber() + " of " + movieResponse.totalPages(),
                new Font(Font.HELVETICA, 10, Font.ITALIC)
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        // Inline display headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=movies-page-" + pageNumber + ".pdf");

        if (email!=null){
        emailService.sendPdfEmail(email,"Movie List of "+movieResponse.pageSize(),"Here is your list of movies pdf","Movie List.pdf", out.toByteArray());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

}
