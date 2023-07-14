package com.capstone.project.domain.iot.service;

import com.capstone.project.domain.PaginatedResponse;
import com.capstone.project.domain.PaginationQueryString;
import com.capstone.project.domain.iot.controller.payload.*;
import com.capstone.project.models.*;
import com.capstone.project.repository.*;
import com.capstone.project.service.SmsService;
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
    private final SmsService smsService;
    private final AlertRepository alertRepository;

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

    @Transactional
    public CompletableFuture<Void> processReceiveMessage(String receivedMessage) {
        String[] processMessage = receivedMessage.split(",");
        if (processMessage.length == 0) {
            return CompletableFuture.completedFuture(null);
        }
        String nodeCode = processMessage[0];
        Node existNode = nodeRepository.findByNodeCode(nodeCode)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.NODE_NOT_FOUND));
        for (int i = 1; i < processMessage.length; i++) {
            String[] sensorData = processMessage[i].split(":");
            if (sensorData.length == 2) {
                String sensorCode = sensorData[0];
                double value = Double.parseDouble(sensorData[1]);

                Sensor matchingSensor = existNode.sensors()
                        .stream()
                        .filter(sensor -> sensor.sensorCode().equals(sensorCode))
                        .findFirst()
                        .orElse(null);

                if (matchingSensor != null) {
                    Date currentTime = new Date();
                    if (value < matchingSensor.minThreshold() || value > matchingSensor.maxThreshold()) {
                        String messageSensorType = getMessageSensorType(matchingSensor);
                        String messageHighLow = "Too High";
                        String messageTemplate = "Sensor " + matchingSensor.sensorCode() + " " + messageSensorType + " has reach " + value + " with limit of [" + matchingSensor.minThreshold() + " ; " + matchingSensor.maxThreshold() + "] " + messageHighLow;
                        Alert alert = Alert.builder()
                                .sensor(matchingSensor)
                                .content(messageTemplate)
                                .isRead(false)
                                .build();
                        alert.createdDate(new Date());
                        alert.updatedDate(new Date());
                        alertRepository.save(alert);
                        matchingSensor.node().users().forEach(user -> {
                            if (user.phone() != null) {
//                                 smsService.sendSMS("+84" + user.phone(), messageTemplate);
                                log.info("Send SMS to " + user.phone() + " with message: " + messageTemplate);
                            }
                        });
                    }

                    SensorIntervalData newData = new SensorIntervalData();
                    newData.data(value);
                    newData.createTimestamp(currentTime);
                    newData.sensor(matchingSensor);
                    sensorIntervalDataRepository.save(newData);
                } else {
                    log.error("Error: Sensor with code " + sensorCode + " not found.");
                }
            } else {
                log.error("Error: Invalid sensor data entry: " + processMessage[i]);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    public String getMessageSensorType(Sensor sensor) {
        switch (sensor.type()) {
            case TEMPERATURE -> {
                return "Temperature";
            }
            case HUMIDITY -> {
                return "Humidity Level";
            }
            case LIGHT -> {
                return "Light Intensity";
            }
            case SMOKE -> {
                return "Smoke Density";
            }
            default -> {
                return "Unknown Sensor Type";
            }
        }
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
    public CompletableFuture<Map<String, SensorListDataResponse>> getSensorOfTypeIntervalData(SensorType sensorType, Date startDate, Date endDate) {
        try (Stream<SensorIntervalData> intervalDatas = Optional.ofNullable(startDate)
                .flatMap(s -> Optional.ofNullable(endDate)
                        .map(e -> sensorIntervalDataRepository.findAllByCreateTimestampBetweenAndSensor_TypeOrderByCreateTimestampAsc(startDate, endDate, sensorType)))
                .orElseGet(() -> sensorIntervalDataRepository.findAllBySensor_TypeOrderByCreateTimestampAsc(sensorType))) {
            Map<String, SensorListDataResponse> responsesByType = new HashMap<>();
            intervalDatas.forEach(data -> {
                String sensorCode = data.sensor().sensorCode();
                SensorListDataResponse response = responsesByType.computeIfAbsent(sensorCode, k -> new SensorListDataResponse());
                response.add(data);
                entityManager.detach(data);
            });
            return CompletableFuture.completedFuture(responsesByType);
        }
    }

    @Transactional(readOnly = true)
    public CompletableFuture<Map<String, SensorListDataResponse>> getSensorOfTypeIntervalDataVer2(SensorType sensorType) {
        try (Stream<SensorIntervalData> intervalDatas = sensorIntervalDataRepository.findAllBySensor_TypeOrderByCreateTimestampAsc(sensorType)) {
            Map<String, SensorListDataResponse> responsesByType = new HashMap<>();
            intervalDatas.forEach(data -> {
                String sensorCode = data.sensor().sensorCode();
                SensorListDataResponse response = responsesByType.computeIfAbsent(sensorCode, k -> new SensorListDataResponse());
                response.add(data);
                entityManager.detach(data);
            });
            return CompletableFuture.completedFuture(responsesByType);
        }
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
    public CompletableFuture<SensorDataVer2Response> getSensorIntervalDataV2(UUID id) {
        List<double[]> responses = new ArrayList<>();
        double[] minMax = { Double.MAX_VALUE, Double.MIN_VALUE };
        double[] sumCount = { 0, 0 };
        try (Stream<SensorIntervalData> intervalDatas = sensorIntervalDataRepository.findAllBySensor_IdOrderByCreateTimestampAsc(id)) {
            intervalDatas.forEach(data -> {
                double[] responseData = new double[2];
                responseData[0] = data.createTimestamp().getTime();
                responseData[1] = data.data();
                // Update min and max values
                minMax[0] = Math.min(minMax[0], responseData[1]);
                minMax[1] = Math.max(minMax[1], responseData[1]);
                // Accumulate sum and count
                sumCount[0] += responseData[1];
                sumCount[1]++;
                responses.add(responseData);
                entityManager.detach(data);
            });
            double average = sumCount[1] > 0 ? Math.floor((sumCount[0] / sumCount[1]) * 100) / (100.0)  : 0;
            double finalMin = sumCount[1] > 0 ? Math.floor(minMax[0] * 100) / (100.0) : 0;
            double finalMax = sumCount[1] > 0 ? Math.floor(minMax[1] * 100) / (100.0) : 0;
            SensorDataVer2Response sensorDataVerResponse = new SensorDataVer2Response(responses, finalMin, finalMax, average);
            return CompletableFuture.completedFuture(sensorDataVerResponse);
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
        boolean sensorExists = existNode.sensors().stream()
                .anyMatch(sensor -> sensor.type().equals(request.type()));
        if (sensorExists) {
            throw new IllegalArgumentException("A sensor with the same type already exists in the node.");
        }
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
        if (request.min() > request.max()) {
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
