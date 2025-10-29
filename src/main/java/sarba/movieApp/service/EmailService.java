package sarba.movieApp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sarba.movieApp.dto.MailBody;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMail(MailBody mailBody){
        SimpleMailMessage msg=new SimpleMailMessage();

        msg.setTo(mailBody.to());
        msg.setFrom("sarbajitsahoo8260@gmail.com");
        msg.setSubject(mailBody.subject());
        msg.setText(mailBody.text());

        javaMailSender.send(msg);
    }

    public void sendHtmlMail(MailBody mailBody) {
        try {
            // 1. Create a MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            // 2. Use MimeMessageHelper to easily set recipients, subject, and content
            // The 'true' argument indicates multipart/mixed (required for HTML)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("sarbajitsahoo8260@gmail.com");
            helper.setTo(mailBody.to());
            helper.setSubject(mailBody.subject());

            String emailContent = mailBody.htmlText();

            if (emailContent != null && !emailContent.isEmpty()) {
                // If htmlText exists, send it as HTML. The 'true' flag here tells the helper it's HTML.
                helper.setText(emailContent, true);
            } else {
                // Fallback to plain text if HTML is not provided
                helper.setText(mailBody.text());
            }

            // 3. Send the message
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // In a real application, you should log this error
            System.err.println("Error sending HTML email to " + mailBody.to() + ": " + e.getMessage());
            // Optionally throw a custom runtime exception here
            throw new RuntimeException("Failed to send email.", e);
        }
    }

    public void sendPdfEmail(String to, String subject, String body,String pdfName, byte[] pdfBytes)
            throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        // Attach the PDF
        helper.addAttachment(pdfName, new ByteArrayResource(pdfBytes));

        javaMailSender.send(message);
        System.out.println("Email sent to " + to);
    }
}
