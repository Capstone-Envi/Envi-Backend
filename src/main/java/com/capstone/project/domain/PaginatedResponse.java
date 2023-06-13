package com.capstone.project.domain;

import java.util.Collection;

public record PaginatedResponse<T>(long count, Collection<T> data) {
}