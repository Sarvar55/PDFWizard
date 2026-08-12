package com.devlab.pdf_wizard.adapter.in.web.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PageableResponseTest {

    @Test
    @DisplayName("should calculate pagination metadata")
    void shouldCalculatePaginationMetadata() {
        PageableResponse<String> response = PageableResponse.of(
                List.of("document-11", "document-12"),
                1,
                10,
                25);

        assertThat(response.content()).containsExactly("document-11", "document-12");
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(25);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isFalse();
        assertThat(response.hasNext()).isTrue();
        assertThat(response.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("should create empty first and last page")
    void shouldCreateEmptyFirstAndLastPage() {
        PageableResponse<String> response = PageableResponse.of(List.of(), 0, 10, 0);

        assertThat(response.totalPages()).isZero();
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("should reject non-positive page size")
    void shouldRejectNonPositivePageSize() {
        assertThatThrownBy(() -> PageableResponse.of(List.of(), 0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be greater than zero");
    }
}
