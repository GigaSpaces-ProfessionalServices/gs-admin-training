package com.gigaspaces.ui.demo.controllers;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.rest.client.java.api.ContainersApi;
import com.gigaspaces.rest.client.java.api.HostsApi;
import com.gigaspaces.rest.client.java.api.ProcessingUnitsApi;
import com.gigaspaces.rest.client.java.api.SpacesApi;
import com.gigaspaces.rest.client.java.invoker.ApiException;
import com.gigaspaces.rest.client.java.invoker.Configuration;
import com.gigaspaces.rest.client.java.model.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.gigaspaces.common.Constants.*;

@RestController
public class DemoController implements Closeable {

    private final ProcessingUnitsApi processingUnitsApi;
    private final ContainersApi containersApi;
    private final HostsApi hostsApi;
    private final SpacesApi spacesApi;
    private HashMap<String, GigaSpace> spacesProxyMap;
    private Admin admin;

    public DemoController() {
        String restServer;
        XapManagerClusterInfo managerClusterInfo = SystemInfo.singleton().getManagerClusterInfo();
        if (!managerClusterInfo.isEmpty()) {
            XapManagerConfig[] servers = managerClusterInfo.getServers().toArray(new XapManagerConfig[0]);
            if (servers.length == 1) {
                restServer = servers[0].getHost();
            } else {
                restServer = servers[0].getHost();
            }
        } else {
            restServer = "localhost";
        }

        System.out.println("Using REST server running on " + restServer);
        Configuration.getDefaultApiClient().setBasePath("http://" + restServer + ":8090/v2");
        processingUnitsApi = new ProcessingUnitsApi();
        containersApi = new ContainersApi();
        hostsApi = new HostsApi();
        spacesApi = new SpacesApi();
        spacesProxyMap = new HashMap<>();
        admin = new AdminFactory().createAdmin();
    }

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

    @GetMapping(value = "/services")
    public List<ProcessingUnit> getServices() throws ApiException {
        return processingUnitsApi.pusGet();
    }


    @PostMapping(value = "/service/scale")
    public String scaleService(@RequestParam String serviceName, @RequestParam String upOrDown) throws ApiException {
        if (upOrDown.equalsIgnoreCase("up")) {
            processingUnitsApi.pusIdInstancesPost(serviceName);
            return "Incremented instance for " + serviceName + " service";
        } else if (upOrDown.equalsIgnoreCase("down")) {
            List<ProcessingUnitInstance> instances = processingUnitsApi.pusIdInstancesGet(serviceName);
            if (instances.size() == 0) {
                throw new IllegalArgumentException("Can't decrement service " + serviceName);
            }
            List<String> sorted = instances.stream().map(ProcessingUnitInstance::getId).sorted().collect(Collectors.toList());
            String instanceIdToRemove = sorted.get(sorted.size() - 1);
            processingUnitsApi.pusIdInstancesInstanceIdDelete(serviceName, instanceIdToRemove);
            return "Instance " + instanceIdToRemove + " was decremented";
        } else {
            throw new IllegalArgumentException("Unhandled behavior for upOrDown=[" + upOrDown + "]");
        }
    }

    @GetMapping("/hosts")
    public List<Host> getHosts() throws ApiException {
        return hostsApi.hostsGet();
    }

    @PostMapping(value = "/container")
    public String createContainer(@RequestParam String hostName) throws ApiException {
        CreateContainerRequest request = new CreateContainerRequest();
        request.setHost(hostName);
        request.setMemory("512m");
        containersApi.containersPost(request);
        return "GSC created on " + hostName;
    }

    @DeleteMapping(value = "/container")
    public String removeContainer(@RequestParam String containerId) throws ApiException {
        containersApi.containersIdDelete(containerId);
        return "GSC " + containerId + " was removed";
    }

    @PostMapping(value = "/instance/unavailable")
    public String markInstanceUnavailable(@RequestParam String serviceName, @RequestParam String instanceId, @RequestParam Integer duration) throws ApiException {
        ProcessingUnit pu = processingUnitsApi.pusIdGet(serviceName);
        ProcessingUnitInstance puInstance;
        try {
            puInstance = processingUnitsApi.pusIdInstancesInstanceIdGet(serviceName, instanceId);
        }
        catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }

        if (pu.getProcessingUnitType().equals(ProcessingUnit.ProcessingUnitTypeEnum.STATEFUL)) {
            String spaceName = pu.getSpaces().get(0);
            Space space = spacesApi.spacesIdGet(spaceName);
            if (space.getTopology().getBackupsPerPartition() == 0) {
                return "The Service " + serviceName + "has no backups, instance " + instanceId + " cannot be unavailable";
            }
            List<SpaceInstance> spacesInstances = spacesApi.spacesIdInstancesGet(spaceName);
            Integer partitionNum = puInstance.getPartitionId();
            List<SpaceInstance> filteredList = spacesInstances.stream().filter(instance -> instance.getPartitionId().equals(partitionNum)).collect(Collectors.toList());
            if (filteredList.size() < 2) {
                return "The partition has no backup, " + instanceId + " cannot be unavailable";
            }
        }

        String containerId = puInstance.getContainerId();
        String hostName = puInstance.getHostId();
        removeContainer(containerId);
        try {
            Thread.sleep(duration * MILLISECONDS_IN_SECOND);
        } catch (InterruptedException ignored) { }

        createContainer(hostName);
        return "Instance ["+instanceId+"] is being started again";
    }


    @PostMapping(value = "/service/cpualert")
    public String triggerCPUAlertOnService(@RequestParam String serviceName, @RequestParam Integer duration) {
        String spaceName;
        try {
            spaceName = processingUnitsApi.pusIdGet(serviceName).getSpaces().get(0);
        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }
        if (!spacesProxyMap.containsKey(spaceName)){
            spacesProxyMap.put(spaceName, new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)).gigaSpace());
        }

        AsyncFuture<Integer> future = spacesProxyMap.get(spaceName).execute(new CPUAlertTask(SPACE_PARTITION, duration));
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
        ProcessingUnit pu;
        try {
            pu = processingUnitsApi.pusIdGet(serviceName);
            spaceName = pu.getSpaces().get(0);

        } catch (Exception e) {
            return "Failed with error: " + e.getMessage();
        }
        if (!spacesProxyMap.containsKey(spaceName)){
            spacesProxyMap.put(spaceName, new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)).gigaSpace());
        }

        AsyncFuture<Integer> future = spacesProxyMap.get(spaceName).execute(new MemoryAlertTask(SPACE_PARTITION, duration));
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
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public void close() {
        admin.close();
        spacesProxyMap.clear();
    }
}