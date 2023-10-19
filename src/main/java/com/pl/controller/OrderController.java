package com.pl.controller;

import com.pl.model.dto.OrderDTO;
import com.pl.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasAnyRole('ADMIN')")

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public OrderDTO findById(@PathVariable long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("")
    public List<OrderDTO> list() {
        return orderService.listOrders();
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderDTO create(@RequestBody Map<String,Object> order) {
        return orderService.createOrder(order);
    }

    @DeleteMapping("/{orderId}")
    public OrderDTO remove(@PathVariable long orderId) {
        return orderService.remove(orderId);
    }
}
