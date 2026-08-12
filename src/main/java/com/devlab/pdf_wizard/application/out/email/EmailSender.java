package com.devlab.pdf_wizard.application.out.email;

import com.devlab.pdf_wizard.application.model.PdfEmailMessage;

public interface EmailSender {

    void send(PdfEmailMessage message);
}
