// retrainingData/controller/RetrainingDataController.java
package com.fifo.compasstep.retrainingData.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.retrainingData.dto.request.UpdateInvalidRequestDTO;
import com.fifo.compasstep.retrainingData.dto.response.RetrainingListDTO;
import com.fifo.compasstep.retrainingData.service.RetrainingDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/retrain")
public class RetrainingDataController {

    private final RetrainingDataService service;

    @GetMapping("/invalid")
    public ApiResponse<RetrainingListDTO> getInvalid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(service.getInvalid(page, size));
    }

    @GetMapping("/valid")
    public ApiResponse<RetrainingListDTO> getValid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(service.getValid(page, size));
    }

    @PatchMapping("/invalid/convert/{dataPKId}")
    public ApiResponse<Void> convertInvalid(
            @PathVariable Long dataPKId,
            @Valid @RequestBody UpdateInvalidRequestDTO request) {
        service.convertInvalidToValid(dataPKId, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/valid/convert/{dataPKId}")
    public ApiResponse<Void> convertValid(@PathVariable Long dataPKId) {
        service.convertValidToInvalid(dataPKId);
        return ApiResponse.success(null);
    }
}
