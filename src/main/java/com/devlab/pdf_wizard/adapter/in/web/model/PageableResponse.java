package com.devlab.pdf_wizard.adapter.in.web.model;

import java.util.List;
import java.util.Objects;

public record PageableResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) {

    public PageableResponse {
        content = List.copyOf(Objects.requireNonNull(content, "Content cannot be null"));

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements cannot be negative");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("Total pages cannot be negative");
        }
    }

    public static <T> PageableResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements) {
        validatePagination(page, size, totalElements);

        int totalPages = calculateTotalPages(totalElements, size);
        boolean first = page == 0;
        boolean hasPrevious = page > 0;
        boolean hasNext = page + 1 < totalPages;
        boolean last = !hasNext;

        return new PageableResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious);
    }

    private static void validatePagination(int page, int size, long totalElements) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements cannot be negative");
        }
    }

    private static int calculateTotalPages(long totalElements, int size) {
        if (totalElements == 0) {
            return 0;
        }

        return (int) ((totalElements + size - 1) / size);
    }
}
