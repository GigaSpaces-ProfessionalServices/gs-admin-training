package com.gigaspaces.ui.demo.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.start.SystemInfo;
import com.gigaspaces.start.manager.XapManagerClusterInfo;
import com.gigaspaces.start.manager.XapManagerConfig;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnitPartition;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.gigaspaces.common.Constants.*;

/**
 * Backend of the demo-app page. Grid operations go through the REST Manager (REST v3, /api/v3 on port 9090).
 * The REST v2 Java client used by earlier versions no longer exists in GigaSpaces 18.0.
 */
@RestController
public class DemoController implements Closeable {

    private static final int DEFAULT_REST_V3_PORT = 9090;

    private final String restBaseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final HashMap<String, GigaSpace> spacesProxyMap;
    private final Admin admin;

    public DemoController() {
        String restServer;
        XapManagerClusterInfo managerClusterInfo = SystemInfo.singleton().getManagerClusterInfo();
        if (!managerClusterInfo.isEmpty()) {
            XapManagerConfig[] servers = managerClusterInfo.getServers().toArray(new XapManagerConfig[0]);
            restServer = servers[0].getHost();
        } else {
            restServer = "localhost";
        }
        int restPort = DEFAULT_REST_V3_PORT;
        String portEnv = System.getenv("GS_REST_V3_PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            restPort = Integer.parseInt(portEnv.trim());
        }

        restBaseUrl = "http://" + restServer + ":" + restPort + "/api/v3";
        System.out.println("Using REST server (REST v3) at " + restBaseUrl);
        httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        objectMapper = new ObjectMapper();
        spacesProxyMap = new HashMap<>();
        admin = new AdminFactory().createAdmin();
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

    // The REST v3 JSON for processing units and hosts is passed through unchanged to main.js
    @GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getServices() {
        return restGet("/pus");
    }

    @PostMapping(value = "/service/scale")
    public String scaleService(@RequestParam String serviceName, @RequestParam String upOrDown) {
        if (upOrDown.equalsIgnoreCase("up")) {
            restPost("/pus/" + serviceName + "/instances", null);
            return "Incremented instance for " + serviceName + " service";
        } else if (upOrDown.equalsIgnoreCase("down")) {
            List<String> instanceIds = new ArrayList<>();
            for (JsonNode instance : readJson(restGet("/pus/" + serviceName + "/instances"))) {
                instanceIds.add(instance.get("id").asText());
            }
            if (instanceIds.isEmpty()) {
                throw new IllegalArgumentException("Can't decrement service " + serviceName);
            }
            Collections.sort(instanceIds);
            String instanceIdToRemove = instanceIds.get(instanceIds.size() - 1);
            restDelete("/pus/" + serviceName + "/instances/" + instanceIdToRemove);
            return "Instance " + instanceIdToRemove + " was decremented";
        } else {
            throw new IllegalArgumentException("Unhandled behavior for upOrDown=[" + upOrDown + "]");
        }
    }

    @GetMapping(value = "/hosts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getHosts() {
        return restGet("/hosts");
    }

    @PostMapping(value = "/container")
    public String createContainer(@RequestParam String hostName) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("host", hostName);
        request.put("memory", "512m");
        restPost("/containers", request.toString());
        return "GSC created on " + hostName;
    }

    @DeleteMapping(value = "/container")
    public String removeContainer(@RequestParam String containerId) {
        restDelete("/containers/" + containerId);
        return "GSC " + containerId + " was removed";
    }

    @PostMapping(value = "/instance/unavailable")
    public String markInstanceUnavailable(@RequestParam String serviceName, @RequestParam String instanceId, @RequestParam Integer duration) {
        JsonNode pu;
        JsonNode puInstance;
        try {
            pu = readJson(restGet("/pus/" + serviceName));
            puInstance = readJson(restGet("/pus/" + serviceName + "/instances/" + instanceId));
        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }

        if ("stateful".equalsIgnoreCase(pu.path("processingUnitType").asText())) {
            String spaceName = pu.get("spaces").get(0).asText();
            JsonNode space = readJson(restGet("/spaces/" + spaceName));
            if (space.path("topology").path("backupsPerPartition").asInt() == 0) {
                return "The Service " + serviceName + " has no backups, instance " + instanceId + " cannot be unavailable";
            }
            int partitionNum = puInstance.path("partitionId").asInt();
            int instancesInPartition = 0;
            for (JsonNode spaceInstance : readJson(restGet("/spaces/" + spaceName + "/instances"))) {
                if (spaceInstance.path("partitionId").asInt() == partitionNum) {
                    instancesInPartition++;
                }
            }
            if (instancesInPartition < 2) {
                return "The partition has no backup, " + instanceId + " cannot be unavailable";
            }
        }

        String containerId = puInstance.get("containerId").asText();
        String hostName = puInstance.get("hostId").asText();
        removeContainer(containerId);
        try {
            Thread.sleep(duration * MILLISECONDS_IN_SECOND);
        } catch (InterruptedException ignored) { }

        createContainer(hostName);
        return "Instance [" + instanceId + "] is being started again";
    }


    @PostMapping(value = "/service/cpualert")
    public String triggerCPUAlertOnService(@RequestParam String serviceName, @RequestParam Integer duration) {
        String spaceName;
        try {
            spaceName = getSpaceName(serviceName);
        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }

        AsyncFuture<Integer> future = getGigaSpace(spaceName).execute(new CPUAlertTask(SPACE_PARTITION, duration));
        try {
            future.get(duration + 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }
        return "CPU alert finished";
    }


    @PostMapping(value = "/service/memoryalert")
    public String triggerMemoryAlertOnService(@RequestParam String serviceName, @RequestParam Integer duration) {
        String spaceName;
        try {
            spaceName = getSpaceName(serviceName);
        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }

        AsyncFuture<Integer> future = getGigaSpace(spaceName).execute(new MemoryAlertTask(SPACE_PARTITION, duration));
        try {
            future.get(duration + 150, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }
        finally {
            ProcessingUnitPartition partitionedPuInstances = admin.getProcessingUnits().getProcessingUnit(serviceName).getPartition(SPACE_PARTITION);
            partitionedPuInstances.getPrimary().getGridServiceContainer().getVirtualMachine().runGc();
            if (partitionedPuInstances.getBackup() != null) {
                partitionedPuInstances.getBackup().getGridServiceContainer().getVirtualMachine().runGc();
            }
        }
        return "Memory alert finished";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestCallException.class)
    public ResponseEntity<String> handleRestCallException(RestCallException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @Override
    public void close() {
        admin.close();
        spacesProxyMap.clear();
    }

    private String getSpaceName(String serviceName) {
        return readJson(restGet("/pus/" + serviceName)).get("spaces").get(0).asText();
    }

    private synchronized GigaSpace getGigaSpace(String spaceName) {
        return spacesProxyMap.computeIfAbsent(spaceName,
                name -> new GigaSpaceConfigurer(new SpaceProxyConfigurer(name)).gigaSpace());
    }

    // ---- REST v3 helpers ----

    private String restGet(String path) {
        return send(HttpRequest.newBuilder(URI.create(restBaseUrl + path)).GET());
    }

    // Write operations (create/delete container, add/remove instance) are asynchronous in REST v3:
    // they return a request id, and the change is visible once GET /api/v3/requests/{id} is "successful".
    private String restPost(String path, String jsonBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(restBaseUrl + path));
        if (jsonBody == null) {
            builder.POST(HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        }
        return send(builder);
    }

    private String restDelete(String path) {
        return send(HttpRequest.newBuilder(URI.create(restBaseUrl + path)).DELETE());
    }

    private String send(HttpRequest.Builder builder) {
        HttpRequest request = builder.timeout(Duration.ofSeconds(60)).build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new RestCallException(request.method() + " " + request.uri() + " returned " + response.statusCode() + ": " + response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new RestCallException(request.method() + " " + request.uri() + " failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestCallException(request.method() + " " + request.uri() + " was interrupted");
        }
    }

    private JsonNode readJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RestCallException("Invalid JSON from REST server: " + e.getMessage());
        }
    }

    static class RestCallException extends RuntimeException {
        RestCallException(String message) {
            super(message);
        }
    }
}
