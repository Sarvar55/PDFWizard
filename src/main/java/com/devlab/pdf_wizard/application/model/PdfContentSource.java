package com.devlab.pdf_wizard.application.model;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface PdfContentSource {

    InputStream openStream() throws IOException;
}
