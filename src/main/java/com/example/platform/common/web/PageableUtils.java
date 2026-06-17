package com.example.platform.common.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableUtils {

    private PageableUtils() {
    }

    public static Pageable sanitizeSort(Pageable pageable, Set<String> allowedProperties) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        Sort filteredSort = Sort.by(
                pageable.getSort().stream()
                        .filter(order -> allowedProperties.contains(order.getProperty()))
                        .toList()
        );

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), filteredSort);
    }
}