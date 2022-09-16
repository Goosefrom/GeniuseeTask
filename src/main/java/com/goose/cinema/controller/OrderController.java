package com.goose.cinema.controller;

import com.goose.cinema.dto.OrderDto;
import com.goose.cinema.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public OrderDto findById(@PathVariable Long id) {return orderService.findById(id);}

    @PostMapping("/all")
    public Page<OrderDto> findAll(@RequestBody(required = false) OrderDto orderDto,
                                  @RequestParam("page") Integer page,
                                  @RequestParam("size") Integer size) {
        return orderService.searchAll(orderDto, PageRequest.of(page, size));
    }

    @PostMapping
    public OrderDto create(@RequestBody OrderDto orderDto) {return orderService.create(orderDto);}

    @PatchMapping
    public OrderDto update(@RequestBody OrderDto orderDto) {return orderService.update(orderDto);}

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("ok");
    }
}
