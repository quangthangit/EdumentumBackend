package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> data;
    private PaginationInfo pagination;

    @Data
    @AllArgsConstructor
    public static class PaginationInfo {
        private int currentPage;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                new PaginationInfo(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.hasNext(),
                        page.hasPrevious()
                )
        );
    }
}