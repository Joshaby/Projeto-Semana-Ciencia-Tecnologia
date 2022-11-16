package br.edu.ifpb.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${default.sender}")
    private String sender;

    private final JavaMailSender javaMailSender;

    public void sendEmail(String email, String message) {
        SimpleMailMessage mailMessage = prepareMailMessage(email, message);
        javaMailSender.send(mailMessage);
    }

    private SimpleMailMessage prepareMailMessage(String email, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom(sender);
        mailMessage.setSubject("Adição de novo livro");
        mailMessage.setSentDate(new Date(System.currentTimeMillis()));
        mailMessage.setText(text);
        return mailMessage;
    }
}
