
package com.example.vendor.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.example.vendor.service.AuditService;
import com.example.vendor.entity.AuditLog;
import com.example.vendor.util.CsvUtil;

@RestController
@RequestMapping("/api/admin/audit")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditLog> all() {
        return service.getAll();
    }

    @GetMapping("/export")
    public void export(HttpServletResponse res) throws Exception {
        // Set content type and headers
        res.setContentType("text/csv");
        res.setCharacterEncoding("UTF-8");
        
        // Add timestamp to filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        res.setHeader("Content-Disposition", "attachment; filename=\"audit_export_" + timestamp + ".csv\"");
        
        // Write header
        res.getWriter().println("userId,action,path,timestamp");
        
        // Write data with proper CSV escaping
        for (AuditLog a : service.getAll()) {
            String row = CsvUtil.createCsvRow(
                a.getUserId(),
                a.getAction(),
                a.getRequestPath(),
                a.getTimestamp() != null ? a.getTimestamp().toString() : ""
            );
            res.getWriter().println(row);
        }
    }
}
