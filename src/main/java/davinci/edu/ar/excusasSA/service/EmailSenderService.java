package davinci.edu.ar.excusasSA.service;

import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    public void sendEmail(String destinationEmail, String originEmail, String affair, String body) {
        System.out.println("=========================================");
        System.out.println("Email Sent Successfully:");
        System.out.println("from: " + originEmail);
        System.out.println("to: " + destinationEmail);
        System.out.println("affair: " + affair);
        System.out.println("body: " + body.substring(0, Math.min(body.length(), 50)) + "...");
        System.out.println("=========================================");
    }
}
