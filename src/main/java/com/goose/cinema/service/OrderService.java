package com.goose.cinema.service;

import com.goose.cinema.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    OrderDto findById(Long id);

    Page<OrderDto> searchAll(OrderDto orderDto, Pageable pageable);

    OrderDto create(OrderDto orderDto);

    OrderDto update(OrderDto orderDto);

    void delete(Long id);
}
