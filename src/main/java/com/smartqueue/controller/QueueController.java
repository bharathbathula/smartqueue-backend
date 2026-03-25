package com.smartqueue.controller;

import com.smartqueue.dto.QueueDTO;
import com.smartqueue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @GetMapping("/status")
    public ResponseEntity<List<QueueDTO>> getQueueStatus() {
        return ResponseEntity.ok(queueService.getQueueStatus());
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<QueueDTO> updateQueueStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(queueService.updateQueueStatus(id, payload.get("status")));
    }
}
