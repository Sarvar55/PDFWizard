package com.devlab.pdf_wizard.application;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.devlab.pdf_wizard.application.in.CreatePdfUseCase;
import com.devlab.pdf_wizard.application.in.DeletePdfUseCase;
import com.devlab.pdf_wizard.application.in.DownloadPdfUseCase;
import com.devlab.pdf_wizard.application.in.GetPdfMetadataUseCase;
import com.devlab.pdf_wizard.application.in.MergePdfUseCase;
import com.devlab.pdf_wizard.application.in.SplitPdfUseCase;
import com.devlab.pdf_wizard.application.in.SendPdfEmailUseCase;
import com.devlab.pdf_wizard.application.in.command.CreatePdfCommand;
import com.devlab.pdf_wizard.application.in.command.DeletePdfCommand;
import com.devlab.pdf_wizard.application.in.command.MergePdfCommand;
import com.devlab.pdf_wizard.application.in.command.SplitPdfCommand;
import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;
import com.devlab.pdf_wizard.application.in.query.DownloadPdfQuery;
import com.devlab.pdf_wizard.application.in.query.GetPdfMetadataQuery;
import com.devlab.pdf_wizard.application.model.PdfDownloadResult;
import com.devlab.pdf_wizard.application.model.PdfGenerationRequest;
import com.devlab.pdf_wizard.application.model.PdfGenerationResponse;
import com.devlab.pdf_wizard.application.model.PdfMergeRequest;
import com.devlab.pdf_wizard.application.model.PdfSplitRequest;
import com.devlab.pdf_wizard.application.out.PdfLoadPort;
import com.devlab.pdf_wizard.application.out.PdfDeletePort;
import com.devlab.pdf_wizard.application.out.PdfSavePort;
import com.devlab.pdf_wizard.application.out.pdf.PdfGenerator;
import com.devlab.pdf_wizard.application.out.pdf.PdfMerger;
import com.devlab.pdf_wizard.application.out.pdf.PdfSplitter;
import com.devlab.pdf_wizard.application.out.email.PdfEmailTaskDispatcher;
import com.devlab.pdf_wizard.application.out.storage.StorageService;
import com.devlab.pdf_wizard.domain.exception.PdfDocumentNotFoundException;
import com.devlab.pdf_wizard.domain.model.ContentType;
import com.devlab.pdf_wizard.domain.model.PdfDocument;

@Service
public class PdfService implements CreatePdfUseCase, GetPdfMetadataUseCase,
        DownloadPdfUseCase, DeletePdfUseCase, MergePdfUseCase, SplitPdfUseCase,
        SendPdfEmailUseCase {

    private final PdfGenerator pdfGenerator;
    private final StorageService storageService;
    private final PdfSavePort pdfSavePort;
    private final PdfLoadPort pdfLoadPort;
    private final PdfDeletePort pdfDeletePort;
    private final PdfMerger pdfMerger;
    private final PdfSplitter pdfSplitter;
    private final PdfEmailTaskDispatcher pdfEmailTaskDispatcher;

    public PdfService(PdfGenerator pdfGenerator, StorageService storageService,
            PdfSavePort pdfSavePort, PdfLoadPort pdfLoadPort,
            PdfDeletePort pdfDeletePort, PdfMerger pdfMerger,
            PdfSplitter pdfSplitter,
            PdfEmailTaskDispatcher pdfEmailTaskDispatcher) {
        this.pdfGenerator = pdfGenerator;
        this.storageService = storageService;
        this.pdfSavePort = pdfSavePort;
        this.pdfLoadPort = pdfLoadPort;
        this.pdfDeletePort = pdfDeletePort;
        this.pdfMerger = pdfMerger;
        this.pdfSplitter = pdfSplitter;
        this.pdfEmailTaskDispatcher = pdfEmailTaskDispatcher;
    }

    @Override
    public PdfDocument execute(CreatePdfCommand command) {
        PdfGenerationRequest request = new PdfGenerationRequest(command.fileName(), command.title(),
                command.data());

        PdfGenerationResponse response = pdfGenerator.generate(request);

        return storeAndSave(response, command.createdBy());
    }

    @Override
    public PdfDocument execute(GetPdfMetadataQuery query) {
        return findDocument(query.id());
    }

    @Override
    public PdfDownloadResult execute(DownloadPdfQuery query) {
        PdfDocument document = findDocument(query.id());
        InputStream content = storageService.load(document.getStoredFileName());

        return new PdfDownloadResult(
                document.getFileName(),
                document.getContentType().getPrimaryMimeType(),
                document.getSize(),
                content);
    }

    @Override
    public void execute(DeletePdfCommand command) {
        PdfDocument document = findDocument(command.id());

        storageService.delete(document.getStoredFileName());
        pdfDeletePort.deleteById(document.getId());
    }

    @Override
    public PdfDocument execute(MergePdfCommand command) {
        PdfMergeRequest request = new PdfMergeRequest(
                command.outputFileName(),
                command.files());
        PdfGenerationResponse response = pdfMerger.merge(request);

        return storeAndSave(response, command.createdBy());
    }

    @Override
    public List<PdfDocument> execute(SplitPdfCommand command) {
        PdfSplitRequest request = new PdfSplitRequest(
                command.outputFileNamePrefix(),
                command.file());

        return pdfSplitter.split(request).stream()
                .map(response -> storeAndSave(response, command.createdBy()))
                .toList();
    }

    @Override
    public void execute(SendPdfEmailCommand command) {
        findDocument(command.documentId());
        pdfEmailTaskDispatcher.dispatch(command);
    }

    private PdfDocument storeAndSave(PdfGenerationResponse response, String createdBy) {
        String storedFileName = storageService.store(
                response.fileName(),
                response.contentType(),
                response.content());

        PdfDocument document = PdfDocument.create(
                response.fileName(),
                storedFileName,
                ContentType.fromMimeType(response.contentType()),
                (long) response.content().length,
                createdBy);

        return pdfSavePort.save(document);
    }

    private PdfDocument findDocument(UUID id) {
        return pdfLoadPort.findById(id)
                .orElseThrow(() -> PdfDocumentNotFoundException.forId(id));
    }
}
