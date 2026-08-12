package com.devlab.pdf_wizard.adapter.out.email;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.in.command.SendPdfEmailCommand;
import com.devlab.pdf_wizard.application.out.email.PdfEmailTaskDispatcher;
import com.devlab.pdf_wizard.domain.exception.EmailDispatchException;

@Component
public class ExecutorPdfEmailTaskDispatcher implements PdfEmailTaskDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorPdfEmailTaskDispatcher.class);

    private final ExecutorService mailExecutor;
    private final PdfEmailWorker worker;

    public ExecutorPdfEmailTaskDispatcher(
            @Qualifier("mailExecutor") ExecutorService mailExecutor,
            PdfEmailWorker worker) {
        this.mailExecutor = mailExecutor;
        this.worker = worker;
    }

    @Override
    public void dispatch(SendPdfEmailCommand command) {
        try {
            mailExecutor.execute(() -> runTask(command));
        } catch (RejectedExecutionException exception) {
            throw new EmailDispatchException("Email task queue is full", exception);
        }
    }

    private void runTask(SendPdfEmailCommand command) {
        try {
            worker.send(command);
        } catch (RuntimeException exception) {
            LOGGER.error(
                    "PDF email task failed for document {} and recipient {}",
                    command.documentId(),
                    command.recipient(),
                    exception);
        }
    }
}
