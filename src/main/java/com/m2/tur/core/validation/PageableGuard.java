package com.m2.tur.core.validation;

import com.m2.tur.core.exception.BusinessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableGuard {

    public void pageableValidate(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!order.getProperty().equals("createdAt")) {
                throw new BusinessException("Dynamic sorting blocked.");
            }
        }
    }
}
