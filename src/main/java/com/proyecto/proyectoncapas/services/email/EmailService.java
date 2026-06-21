package com.proyecto.proyectoncapas.services.email;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String htmlBody);
}
