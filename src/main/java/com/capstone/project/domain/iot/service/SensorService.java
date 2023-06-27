package com.capstone.project.domain.iot.service;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.iot.controller.payload.*;
import com.capstone.project.models.*;
import com.capstone.project.repository.NodeRepository;
import com.capstone.project.repository.SensorIntervalDataRepository;
import com.capstone.project.repository.SensorPeriodicDataRepository;
import com.capstone.project.repository.SensorRepository;
import com.capstone.project.utils.ExceptionMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sensorRepository;
    private final NodeRepository nodeRepository;
    private final SensorIntervalDataRepository sensorIntervalDataRepository;
    private final SensorPeriodicDataRepository sensorPeriodicDataRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public CompletableFuture<PaginatedResponse<SensorResponse>> getSensorsByNodeId(UUID nodeId) {
        Node existNode = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND));

        List<Sensor> sensors = existNode.sensors();
        sensors.sort(Comparator.comparing(Sensor::sensorCode));
        return CompletableFuture.completedFuture(new PaginatedResponse<>(
                sensors.size(),
                sensors.stream().map(SensorResponse::new).toList()
        ));
    }

    @Transactional(readOnly = true)
    public CompletableFuture<PaginatedResponse<SensorResponse>> getSensors(
            User currentUser,
            PaginationQueryString queryString,
            SensorType type) {
        Page<Sensor> sensors;
        Long count;
        if (currentUser.role().getName().equals(RoleName.USER)) {
            if (type != null) {
                sensors = sensorRepository
                        .findByNode_Users_IdAndTypeOrderBySensorCode(currentUser.id(), type, queryString.getPageable());
                count = sensorRepository.countByNode_Users_IdAndType(currentUser.id(), type);
            } else {
                sensors = sensorRepository
                        .findByNode_Users_IdOrderBySensorCode(currentUser.id(), queryString.getPageable());
                count = sensorRepository.countByNode_Users_Id(currentUser.id());
            }
        } else {
            sensors = sensorRepository.findAllByOrderBySensorCode(queryString.getPageable());
            count = sensorRepository.count();
        }

        return CompletableFuture.completedFuture(new PaginatedResponse<>(
                count,
                sensors.getContent().stream().map(SensorResponse::new).toList()
        ));
    }

    @Transactional(readOnly = true)
    public CompletableFuture<SensorResponse> getSensor(UUID id) {
        return CompletableFuture.completedFuture(
                sensorRepository.findById(id)
                        .map(SensorResponse::new)
                        .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SENSOR_NOT_FOUND))
        );
    }

    @Transactional(readOnly = true)
    public CompletableFuture<SensorListDataResponse> getSensorIntervalData(UUID id) {
        SensorListDataResponse responses = new SensorListDataResponse();
        try (Stream<SensorIntervalData> intervalDatas = sensorIntervalDataRepository.findAllBySensor_IdOrderByCreateTimestampAsc(id)) {
            intervalDatas.forEach(data -> {
                responses.add(data);
                entityManager.detach(data);
            });
            return CompletableFuture.completedFuture(responses);
        }
    }

    @Transactional(readOnly = true)
    public CompletableFuture<SensorDataResponse> getLatestIntervalData(UUID id) {
        SensorIntervalData latestData = sensorIntervalDataRepository.findFirstBySensor_IdOrderByCreateTimestampDesc(id);
        return CompletableFuture.completedFuture(latestData != null ? new SensorDataResponse(latestData) : null);
    }

    @Transactional(readOnly = true)
    public CompletableFuture<SensorListDataResponse> getSensorPeriodicData(UUID id) {
        SensorListDataResponse responses = new SensorListDataResponse();
        try (Stream<SensorPeriodicData> intervalDatas = sensorPeriodicDataRepository.findAllBySensor_IdOrderByCreateTimestampAsc(id)) {
            intervalDatas.forEach(data -> {
                responses.add(data);
                entityManager.detach(data);
            });
            return CompletableFuture.completedFuture(responses);
        }
    }

    @Transactional(readOnly = true)
    public CompletableFuture<SensorDataResponse> getLatestPeriodicData(UUID id) {
        SensorPeriodicData latestData = sensorPeriodicDataRepository.findFirstBySensor_IdOrderByCreateTimestampDesc(id);
        return CompletableFuture.completedFuture(latestData != null ? new SensorDataResponse(latestData) : null);
    }

    @Transactional
    public CompletableFuture<SensorResponse> createSensor(UUID id, SensorCreateRequest request) {
        Node existNode = nodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND));
        if (existNode.sensors().size() >= 4) {
            throw new IllegalArgumentException(ExceptionMessage.MAXIMUM_NODE_SENSOR_ERROR);
        }
        this.validateSensorCode(request.sensorCode());
        Sensor newSensor = request.toSensor();
        newSensor.createdDate(new Date());
        newSensor.updatedDate(new Date());
        newSensor.node(existNode);
        return CompletableFuture.completedFuture(
                new SensorResponse(sensorRepository.save(newSensor))
        );
    }

    @Transactional
    public CompletableFuture<SensorResponse> updateSensor(UUID id, SensorUpdateRequest request) {
        Sensor updatedSensor = sensorRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SENSOR_NOT_FOUND));
        updatedSensor.type(request.type());
        updatedSensor.power(request.power());
        updatedSensor.size(request.size());
        updatedSensor.productLine(request.productLine());
        updatedSensor.location(request.location());
        updatedSensor.interval(request.interval());
        updatedSensor.updatedDate(new Date());
        return CompletableFuture.completedFuture(
                new SensorResponse(sensorRepository.save(updatedSensor))
        );
    }

    @Transactional
    public CompletableFuture<SensorResponse> updateSensorThreshold(UUID id, SensorUpdateThresholdRequest request) {
        Sensor updatedSensor = sensorRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.SENSOR_NOT_FOUND));
        if(request.min() > request.max()) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_THRESHOLD);
        }
        updatedSensor.minThreshold(request.min());
        updatedSensor.maxThreshold(request.max());
        updatedSensor.updatedDate(new Date());
        //TODO-Send Alert If the latest value is out of new threshold
        return CompletableFuture.completedFuture(
                new SensorResponse(sensorRepository.save(updatedSensor))
        );
    }

    @Transactional
    public CompletableFuture<Void> deleteSensor(UUID id) {
        sensorRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    private void validateSensorCode(String sensorCode) {
        if (sensorRepository.existsBySensorCode(sensorCode)) {
            throw new IllegalArgumentException("SensorCode already exists.");
        }
    }
}
