package com.capstone.project.domain.user.controller;

import java.util.Collection;

public record PaginatedResponse<T>(long count, Collection<T> data) {
}