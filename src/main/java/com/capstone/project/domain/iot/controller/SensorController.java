package com.capstone.project.domain.iot.controller;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.iot.controller.payload.*;
import com.capstone.project.domain.iot.service.SensorService;
import com.capstone.project.domain.user.service.UserService;
import com.capstone.project.models.SensorType;
import com.capstone.project.models.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class SensorController {
    private final SensorService sensorService;
    private final UserService userService;

    @GetMapping("/api/sensors")
    public CompletableFuture<ResponseEntity<PaginatedResponse<SensorResponse>>> getSensors(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "type", required = false) SensorType type) {
        PaginationQueryString queryString = new PaginationQueryString(offset, limit);
        User currentUser = userService.getCurrentUser();
        return sensorService.getSensors(currentUser, queryString, type)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/node/{nodeId}/sensors")
    public CompletableFuture<ResponseEntity<PaginatedResponse<SensorResponse>>> getSensorsByNodeId(
            @PathVariable UUID nodeId) {
        return sensorService.getSensorsByNodeId(nodeId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/sensor/{id}")
    public CompletableFuture<ResponseEntity<SensorResponse>> getSensor(@PathVariable UUID id) {
        return sensorService.getSensor(id)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/sensor/{sensorId}/data/interval")
    public CompletableFuture<ResponseEntity<SensorListDataResponse>> getSensorIntervalData(@PathVariable UUID sensorId) {
        return sensorService.getSensorIntervalData(sensorId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/v2/sensor/{sensorId}/data/interval")
    public CompletableFuture<ResponseEntity<SensorDataVer2Response>> getSensorIntervalDataVer2(@PathVariable UUID sensorId) {
        return sensorService.getSensorIntervalDataV2(sensorId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/sensor/type/{sensorType}/data/interval")
    public CompletableFuture<ResponseEntity<Map<String, SensorListDataResponse>>> getSensorOfTypeIntervalData(
            @PathVariable SensorType sensorType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") Date endDate) {
        return sensorService.getSensorOfTypeIntervalData(sensorType, startDate, endDate)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/sensor/{sensorId}/data/interval/latest")
    public CompletableFuture<ResponseEntity<SensorDataResponse>> getLatestSensorIntervalData(@PathVariable UUID sensorId) {
        return sensorService.getLatestIntervalData(sensorId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/sensor/{sensorId}/data/periodic")
    public CompletableFuture<ResponseEntity<SensorListDataResponse>> getSensorPeriodicData(@PathVariable UUID sensorId) {
        return sensorService.getSensorPeriodicData(sensorId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/api/sensor/{sensorId}/data/periodic/latest")
    public CompletableFuture<ResponseEntity<SensorDataResponse>> getLatestSensorPeriodicData(@PathVariable UUID sensorId) {
        return sensorService.getLatestPeriodicData(sensorId)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/api/node/{nodeId}/sensor")
    public CompletableFuture<ResponseEntity<SensorResponse>> createSensor(
            @PathVariable UUID nodeId,
            @RequestBody SensorCreateRequest request) {
        return sensorService.createSensor(nodeId, request)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/api/sensor/{id}")
    public CompletableFuture<ResponseEntity<SensorResponse>> updateSensor(@PathVariable UUID id,
                                                                          @RequestBody SensorUpdateRequest request) {
        return sensorService.updateSensor(id, request)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/api/sensor/{id}/threshold")
    public CompletableFuture<ResponseEntity<SensorResponse>> updateSensorThreshold(@PathVariable UUID id,
                                                                          @RequestBody SensorUpdateThresholdRequest request) {
        return sensorService.updateSensorThreshold(id, request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/api/sensor/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteSensor(@PathVariable UUID id) {
        return sensorService.deleteSensor(id)
                .thenApply(deleted -> ResponseEntity.noContent().build());
    }
}
