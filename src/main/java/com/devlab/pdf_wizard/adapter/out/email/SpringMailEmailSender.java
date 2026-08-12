package com.devlab.pdf_wizard.adapter.out.email;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.model.PdfEmailMessage;
import com.devlab.pdf_wizard.application.out.email.EmailSender;
import com.devlab.pdf_wizard.domain.exception.EmailSendingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class SpringMailEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final String senderAddress;

    public SpringMailEmailSender(
            JavaMailSender mailSender,
            @Value("${pdf-wizard.mail.from:no-reply@pdfwizard.local}") String senderAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    @Override
    public void send(PdfEmailMessage message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name());
            helper.setFrom(senderAddress);
            helper.setTo(message.recipient());
            helper.setSubject(message.subject());
            helper.setText(message.body(), false);
            helper.addAttachment(
                    message.attachmentFileName(),
                    new ByteArrayResource(message.attachmentContent()),
                    message.attachmentContentType());

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            throw new EmailSendingException("PDF email could not be created", exception);
        }
    }
}
