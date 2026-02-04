package com.graphapi.aem.core.services;

public interface MailService {
    String sendMail(String to, String subject, String message);
}
