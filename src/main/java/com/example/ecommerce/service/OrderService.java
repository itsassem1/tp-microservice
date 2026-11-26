package com.example.ecommerce.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        ClientService clientService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.productService = productService;
    }

    public Order createOrder(Order request) {
        List<String> productIds = request.getProductIds();

    
        if (productIds == null || productIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "productIds must contain at least one id");
        }
        if (new HashSet<>(productIds).size() != productIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "productIds must not contain duplicates");
        }
        if (request.getClientId() == null || request.getClientId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "clientId is required");
        }

        
        clientService.getClientById(request.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found: " + request.getClientId()));

        
        double total = 0;
        for (String productId : productIds) {
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found: " + productId));
            total += product.getPrice();
        }

        
        Order order = new Order(request.getClientId(), productIds, total);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }
}