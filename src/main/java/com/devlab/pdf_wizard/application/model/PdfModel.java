package com.devlab.pdf_wizard.application.model;

public interface PdfModel<Req, Res> {
    Res generate(Req req);
}
