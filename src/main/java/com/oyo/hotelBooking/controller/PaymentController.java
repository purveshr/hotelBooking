package com.oyo.hotelBooking.controller;

import com.oyo.hotelBooking.dtos.PaymentRequestDTO;
import com.oyo.hotelBooking.dtos.PaymentResponseDTO;
import com.oyo.hotelBooking.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment API", description = "Operations related to payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Create a payment", description = "Creates a payment for a booking")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO response = paymentService.createPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get payment by id", description = "Get payment details by id")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}

