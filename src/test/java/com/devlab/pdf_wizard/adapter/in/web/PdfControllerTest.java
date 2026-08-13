package com.devlab.pdf_wizard.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.devlab.pdf_wizard.application.in.CreatePdfUseCase;
import com.devlab.pdf_wizard.application.in.DeletePdfUseCase;
import com.devlab.pdf_wizard.application.in.DownloadPdfUseCase;
import com.devlab.pdf_wizard.application.in.GetPdfMetadataUseCase;
import com.devlab.pdf_wizard.application.in.MergePdfUseCase;
import com.devlab.pdf_wizard.application.in.SplitPdfUseCase;
import com.devlab.pdf_wizard.application.in.SendPdfEmailUseCase;
import com.devlab.pdf_wizard.application.in.command.MergePdfCommand;
import com.devlab.pdf_wizard.application.in.command.SplitPdfCommand;
import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;
import com.devlab.pdf_wizard.application.model.PdfDownloadResult;
import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

class PdfControllerTest {

    private MockMvc mockMvc;
    private PdfDocument document;
    private AtomicReference<UUID> deletedDocumentId;
    private AtomicReference<MergePdfCommand> mergeCommand;
    private AtomicReference<SplitPdfCommand> splitCommand;
    private AtomicReference<SendPdfEmailCommand> emailCommand;

    @BeforeEach
    void setUp() {
        document = PdfDocument.create(
                "invoice.pdf",
                "documents/invoice.pdf",
                ContentType.PDF,
                256L,
                "sarvar");
        CreatePdfUseCase createPdfUseCase = command -> document;
        GetPdfMetadataUseCase getPdfMetadataUseCase = query -> document;
        DownloadPdfUseCase downloadPdfUseCase = query -> new PdfDownloadResult(
                "invoice.pdf",
                "application/pdf",
                3,
                new ByteArrayInputStream(new byte[] { 1, 2, 3 }));
        deletedDocumentId = new AtomicReference<>();
        DeletePdfUseCase deletePdfUseCase = command -> deletedDocumentId.set(command.id());
        mergeCommand = new AtomicReference<>();
        MergePdfUseCase mergePdfUseCase = command -> {
            mergeCommand.set(command);
            return document;
        };
        splitCommand = new AtomicReference<>();
        SplitPdfUseCase splitPdfUseCase = command -> {
            splitCommand.set(command);
            return List.of(document);
        };
        emailCommand = new AtomicReference<>();
        SendPdfEmailUseCase sendPdfEmailUseCase = emailCommand::set;
        PdfController controller = new PdfController(
                createPdfUseCase,
                getPdfMetadataUseCase,
                downloadPdfUseCase,
                deletePdfUseCase,
                mergePdfUseCase,
                splitPdfUseCase,
                sendPdfEmailUseCase);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("should download PDF content with HTTP 200")
    void shouldDownloadPdfContentWithHttp200() throws Exception {
        mockMvc.perform(get("/api/pdf/{id}/download", document.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Length", "3"))
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"invoice.pdf\""));
    }

    @Test
    @DisplayName("should return PDF metadata with HTTP 200")
    void shouldReturnPdfMetadataWithHttp200() throws Exception {
        mockMvc.perform(get("/api/pdf/{id}", document.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(document.getId().toString()))
                .andExpect(jsonPath("$.fileName").value("invoice.pdf"))
                .andExpect(jsonPath("$.storedFileName").value("documents/invoice.pdf"))
                .andExpect(jsonPath("$.contentType").value("PDF"))
                .andExpect(jsonPath("$.size").value(256))
                .andExpect(jsonPath("$.createdBy").value("sarvar"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("should delete PDF document with HTTP 204")
    void shouldDeletePdfDocumentWithHttp204() throws Exception {
        mockMvc.perform(delete("/api/pdf/{id}", document.getId()))
                .andExpect(status().isNoContent());

        assertThat(deletedDocumentId.get()).isEqualTo(document.getId());
    }

    @Test
    @DisplayName("should merge uploaded PDFs with HTTP 201")
    void shouldMergeUploadedPdfsWithHttp201() throws Exception {
        MockMultipartFile first = new MockMultipartFile(
                "files", "first.pdf", "application/pdf", new byte[] { 1 });
        MockMultipartFile second = new MockMultipartFile(
                "files", "second.pdf", "application/pdf", new byte[] { 2 });

        mockMvc.perform(multipart("/api/pdf/merge")
                        .file(first)
                        .file(second)
                        .param("outputFileName", "merged.pdf"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/pdf/" + document.getId()))
                .andExpect(jsonPath("$.id").value(document.getId().toString()));

        assertThat(mergeCommand.get().outputFileName()).isEqualTo("merged.pdf");
        assertThat(mergeCommand.get().files()).hasSize(2);
        assertThat(mergeCommand.get().createdBy()).isEqualTo("system");
    }

    @Test
    @DisplayName("should split uploaded PDF with HTTP 201")
    void shouldSplitUploadedPdfWithHttp201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "contract.pdf", "application/pdf", new byte[] { 1 });

        mockMvc.perform(multipart("/api/pdf/split")
                        .file(file)
                        .param("outputFileNamePrefix", "contract"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(document.getId().toString()))
                .andExpect(jsonPath("$[0].fileName").value(document.getFileName()));

        assertThat(splitCommand.get().outputFileNamePrefix()).isEqualTo("contract");
        assertThat(splitCommand.get().file().fileName()).isEqualTo("contract.pdf");
        assertThat(splitCommand.get().createdBy()).isEqualTo("system");
    }

    @Test
    @DisplayName("should accept PDF email task with HTTP 202")
    void shouldAcceptPdfEmailTaskWithHttp202() throws Exception {
        String requestBody = """
                {
                  "documentId": "%s",
                  "recipient": "user@example.com"
                }
                """.formatted(document.getId());

        mockMvc.perform(post("/api/pdf/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());

        assertThat(emailCommand.get().documentId()).isEqualTo(document.getId());
        assertThat(emailCommand.get().recipient()).isEqualTo("user@example.com");
    }
}
