
package com.example.vendor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.List;
import com.example.vendor.service.TestService;
import com.example.vendor.dto.TestDTO;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TestDTO> create(@Valid @RequestBody TestDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<TestDTO>> all() {
        return ResponseEntity.ok(service.findAll());
    }
}
