package Tools;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;

public class Mail {

    final String username = "buroomie@gmail.com";
    final String password = System.getenv("MAIL_KEY");
    Properties props = new Properties();

    public Mail() {
        props.put("mail.smtp.host", "smtp.gmail.com");  // SMTP Server
        props.put("mail.smtp.port", "587");             // TLS Port
        props.put("mail.smtp.auth", "true");            // Enable Authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
    }

    public boolean send(String recipient, String subject, String body) {
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println();

        } catch (MessagingException e) {
            System.out.println("Error while sending mail: " + e.getMessage());
            return false;
        }
        return true;
    }
}
