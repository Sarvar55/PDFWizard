package com.devlab.pdf_wizard.adapter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.model.PdfGenerationRequest;
import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.out.pdf.PdfGenerator;
import com.devlab.pdf_wizard.domain.exception.PdfGenerationException;

@Component
public class PdfBoxPdfGenerator implements PdfGenerator {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final float MARGIN = 50;
    private static final float TITLE_FONT_SIZE = 18;
    private static final float BODY_FONT_SIZE = 12;
    private static final float TITLE_SPACING = 30;
    private static final float LINE_SPACING = 18;
    private static final int MAX_CHARACTERS_PER_LINE = 80;

    @Override
    public PdfGenerationResponse generate(PdfGenerationRequest request) {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            List<String> lines = createContentLines(request.data());

            writePages(document, request.title(), lines, titleFont, bodyFont);
            document.save(output);

            return new PdfGenerationResponse(
                    request.fileName(),
                    PDF_CONTENT_TYPE,
                    output.toByteArray());
        } catch (IOException | IllegalArgumentException exception) {
            throw new PdfGenerationException("PDF document could not be generated", exception);
        }
    }

    private void writePages(PDDocument document, String title, List<String> lines,
            PDType1Font titleFont, PDType1Font bodyFont) throws IOException {
        int lineIndex = 0;
        boolean firstPage = true;

        do {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);

                if (firstPage) {
                    contentStream.setFont(titleFont, TITLE_FONT_SIZE);
                    contentStream.showText(normalizeText(title));
                    contentStream.newLineAtOffset(0, -TITLE_SPACING);
                }

                contentStream.setFont(bodyFont, BODY_FONT_SIZE);
                float availableHeight = page.getMediaBox().getHeight() - (2 * MARGIN)
                        - (firstPage ? TITLE_SPACING : 0);
                int maximumLines = (int) (availableHeight / LINE_SPACING);

                while (lineIndex < lines.size() && maximumLines > 0) {
                    contentStream.showText(lines.get(lineIndex));
                    contentStream.newLineAtOffset(0, -LINE_SPACING);
                    lineIndex++;
                    maximumLines--;
                }

                contentStream.endText();
            }

            firstPage = false;
        } while (lineIndex < lines.size());
    }

    private List<String> createContentLines(Map<String, Object> data) {
        List<String> lines = new ArrayList<>();

        data.forEach((key, value) -> {
            String line = normalizeText(key + ": " + String.valueOf(value));
            lines.addAll(wrap(line));
        });

        return lines;
    }

    private List<String> wrap(String text) {
        List<String> lines = new ArrayList<>();
        String remaining = text;

        while (remaining.length() > MAX_CHARACTERS_PER_LINE) {
            int breakPoint = remaining.lastIndexOf(' ', MAX_CHARACTERS_PER_LINE);
            if (breakPoint <= 0) {
                breakPoint = MAX_CHARACTERS_PER_LINE;
            }

            lines.add(remaining.substring(0, breakPoint).trim());
            remaining = remaining.substring(breakPoint).trim();
        }

        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }

        return lines;
    }

    private String normalizeText(String text) {
        return text.replaceAll("[\\r\\n\\t]+", " ").trim();
    }
}
