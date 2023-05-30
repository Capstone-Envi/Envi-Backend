package com.capstone.project.domain.user.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PaginationQueryString (int offset, int limit){
    public PaginationQueryString {
        if (offset < 0) {
            offset = 0;
        }

        if (limit < 0 || limit > 100) {
            limit = 10;
        }
    }

    public Pageable getPageable() {
        return PageRequest.of(offset, limit);
    }
}
