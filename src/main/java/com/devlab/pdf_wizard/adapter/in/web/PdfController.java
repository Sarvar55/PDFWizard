package com.devlab.pdf_wizard.adapter.in.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devlab.pdf_wizard.adapter.in.web.model.CreatePdfRequest;
import com.devlab.pdf_wizard.adapter.in.web.model.PdfMetadataResponse;
import com.devlab.pdf_wizard.adapter.in.web.model.SendPdfEmailRequest;
import com.devlab.pdf_wizard.application.in.CreatePdfUseCase;
import com.devlab.pdf_wizard.application.in.DeletePdfUseCase;
import com.devlab.pdf_wizard.application.in.GetPdfMetadataUseCase;
import com.devlab.pdf_wizard.application.in.MergePdfUseCase;
import com.devlab.pdf_wizard.application.in.SplitPdfUseCase;
import com.devlab.pdf_wizard.application.in.SendPdfEmailUseCase;
import com.devlab.pdf_wizard.application.in.command.DeletePdfCommand;
import com.devlab.pdf_wizard.application.in.command.MergePdfCommand;
import com.devlab.pdf_wizard.application.in.command.SplitPdfCommand;
import com.devlab.pdf_wizard.application.in.query.GetPdfMetadataQuery;
import com.devlab.pdf_wizard.application.model.UploadedPdf;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private static final String DEFAULT_CREATOR = "system";

    private final CreatePdfUseCase createPdfUseCase;
    private final GetPdfMetadataUseCase getPdfMetadataUseCase;
    private final DeletePdfUseCase deletePdfUseCase;
    private final MergePdfUseCase mergePdfUseCase;
    private final SplitPdfUseCase splitPdfUseCase;
    private final SendPdfEmailUseCase sendPdfEmailUseCase;

    public PdfController(CreatePdfUseCase createPdfUseCase,
            GetPdfMetadataUseCase getPdfMetadataUseCase,
            DeletePdfUseCase deletePdfUseCase,
            MergePdfUseCase mergePdfUseCase,
            SplitPdfUseCase splitPdfUseCase,
            SendPdfEmailUseCase sendPdfEmailUseCase) {
        this.createPdfUseCase = createPdfUseCase;
        this.getPdfMetadataUseCase = getPdfMetadataUseCase;
        this.deletePdfUseCase = deletePdfUseCase;
        this.mergePdfUseCase = mergePdfUseCase;
        this.splitPdfUseCase = splitPdfUseCase;
        this.sendPdfEmailUseCase = sendPdfEmailUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<PdfMetadataResponse> create(@Valid @RequestBody CreatePdfRequest request) {
        PdfDocument document = createPdfUseCase.execute(request.toCommand());
        PdfMetadataResponse response = PdfMetadataResponse.from(document);
        URI location = URI.create("/api/pdf/" + document.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PdfMetadataResponse> getMetadata(@PathVariable UUID id) {
        GetPdfMetadataQuery query = GetPdfMetadataQuery.of(id);
        PdfDocument document = getPdfMetadataUseCase.execute(query);
        PdfMetadataResponse response = PdfMetadataResponse.from(document);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deletePdfUseCase.execute(DeletePdfCommand.of(id));

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/merge", consumes = "multipart/form-data")
    public ResponseEntity<PdfMetadataResponse> merge(
            @RequestParam("outputFileName") String outputFileName,
            @RequestParam("files") List<MultipartFile> files) {
        List<UploadedPdf> uploadedPdfs = files.stream()
                .map(this::toUploadedPdf)
                .toList();
        MergePdfCommand command = MergePdfCommand.of(
                outputFileName,
                uploadedPdfs,
                DEFAULT_CREATOR);
        PdfDocument document = mergePdfUseCase.execute(command);
        PdfMetadataResponse response = PdfMetadataResponse.from(document);
        URI location = URI.create("/api/pdf/" + document.getId());

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping(value = "/split", consumes = "multipart/form-data")
    public ResponseEntity<List<PdfMetadataResponse>> split(
            @RequestParam("outputFileNamePrefix") String outputFileNamePrefix,
            @RequestParam("file") MultipartFile file) {
        SplitPdfCommand command = SplitPdfCommand.of(
                outputFileNamePrefix,
                toUploadedPdf(file),
                DEFAULT_CREATOR);
        List<PdfMetadataResponse> response = splitPdfUseCase.execute(command).stream()
                .map(PdfMetadataResponse::from)
                .toList();

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/send-email")
    public ResponseEntity<Void> sendEmail(
            @Valid @RequestBody SendPdfEmailRequest request) {
        sendPdfEmailUseCase.execute(request.toCommand());

        return ResponseEntity.accepted().build();
    }

    private UploadedPdf toUploadedPdf(MultipartFile file) {
        return UploadedPdf.of(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file::getInputStream);
    }
}
