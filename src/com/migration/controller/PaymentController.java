package com.migration.controller;

import com.migration.model.PaymentRequest;
import com.migration.model.Address;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final List<String> SUPPORTED_COUNTRIES = Arrays.asList("USA", "Canada", "Mexico");
    private static final Double MAX_PAYMENT_AMOUNT = 10000.0;
    private Map<String, List<String>> processedPayments = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequest request) {
        Map<String, String> response = new HashMap<>();

        // Validate payment amount
        if (request.getAmount() > MAX_PAYMENT_AMOUNT) {
            response.put("error", "Payment amount exceeds maximum limit of " + MAX_PAYMENT_AMOUNT);
            return ResponseEntity.badRequest().body(response);
        }

        // Validate country
        if (!isValidCountry(request.getAddress())) {
            response.put("error", "Country not supported. Supported countries: " + String.join(", ", SUPPORTED_COUNTRIES));
            return ResponseEntity.badRequest().body(response);
        }

        // Process payment based on amount ranges
        String processingResult = processPaymentAmount(request);
        if (processingResult != null) {
            // Store processed payment
            storeProcessedPayment(request);

            response.put("status", "success");
            response.put("message", processingResult);
            return ResponseEntity.ok(response);
        }

        response.put("error", "Payment processing failed");
        return ResponseEntity.badRequest().body(response);
    }

    private boolean isValidCountry(Address address) {
        return SUPPORTED_COUNTRIES.stream()
                .anyMatch(country -> country.equalsIgnoreCase(address.getCountry()));
    }

    private String processPaymentAmount(PaymentRequest request) {
        Double amount = request.getAmount();

        if (amount <= 0) {
            return null;
        }

        // Different processing logic based on amount ranges
        if (amount <= 100) {
            return "Small payment processed instantly";
        } else if (amount <= 1000) {
            return "Medium payment processed with standard verification";
        } else {
            // Simulate additional verification for large payments
            for (int i = 0; i < 3; i++) {
                if (verifyLargePayment(request)) {
                    return "Large payment processed with enhanced verification";
                }
            }
            return null;
        }
    }

    private boolean verifyLargePayment(PaymentRequest request) {
        // Simulate verification logic
        return request.getAccountNumber() != null &&
               request.getRoutingNumber() != null &&
               request.getBeneficiaryName() != null;
    }

    private void storeProcessedPayment(PaymentRequest request) {
        String key = request.getBeneficiaryName();
        processedPayments.computeIfAbsent(key, k -> new ArrayList<>())
            .add(String.format("Amount: $%.2f, Date: %s",
                request.getAmount(),
                new Date()));
    }

    @GetMapping("/history/{beneficiaryName}")
    public ResponseEntity<?> getPaymentHistory(@PathVariable String beneficiaryName) {
        List<String> history = processedPayments.get(beneficiaryName);

        if (history == null || history.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No payment history found");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(history);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
