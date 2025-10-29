package sarba.movieApp.controllers;

import jakarta.mail.MessagingException;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarba.movieApp.service.EmailService;

import java.awt.*;
import java.io.ByteArrayOutputStream;

import static org.openpdf.text.PageSize.*;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send/{email}/{text}")
    public ResponseEntity<byte[]> sendPdf(@PathVariable String email, @PathVariable String text) {
        try {
            // Create a byte output stream (in-memory PDF)
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Create document and writer linked to memory
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            HeaderFooter header = new HeaderFooter( new Phrase("Sarba Movie"),false);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setBorderWidthBottom(2);
            header.setBorderColor(Color.red);
            header.setBorderWidthTop(0);
            document.setHeader(header);

            HeaderFooter footer = new HeaderFooter(true, new Phrase(" page"));
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setBorderWidthBottom(0);
            document.setFooter(footer);
            document.setPageSize(A4.rotate());

            document.open();
            document.add(new Paragraph(text));
            document.close();

            // Convert to byte array
            byte[] pdfBytes = out.toByteArray();

//            emailService.sendPdfEmail(email, "Your PDF File", "Here’s your generated PDF file.", pdfBytes);

            // Set headers for browser download/display
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=HelloWorld.pdf")
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=HelloWorld.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
