// src/main/java/com/fifo/compasstep/chat/controller/DiscoveryController.java
package com.fifo.compasstep.reference.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.reference.dto.request.DiscoveryKeywordRequestDto;
import com.fifo.compasstep.reference.dto.response.TrackVideoDto;
import com.fifo.compasstep.reference.service.DiscoveryService;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class DiscoveryController {

    private final DiscoveryService service;

    /** 키워드 기반 레퍼런스 탐색 (FastAPI 프록시) */
    @PostMapping("/discovery/keyword")
    @PreAuthorize("hasAuthority('STATUS_NORMAL')")
    public ApiResponse<List<TrackVideoDto>> discoveryKeyword(
            @Valid @RequestBody DiscoveryKeywordRequestDto req,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        return service.discover(userId, req.query());
    }
}
