package com.gs.admin.relocate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.admin.*;
import org.openspaces.admin.gsa.*;
import org.openspaces.admin.gsc.*;
import org.openspaces.admin.pu.*;
import java.util.concurrent.TimeUnit;


public class Relocate {
    private static final Log log = LogFactory.getLog(Relocate.class);

    public static void main(String[] args) throws InterruptedException {

        int delay = 20000;
        AdminFactory factory = new AdminFactory();
        factory.addGroup(System.getenv("GS_LOOKUP_GROUPS"));
        Admin admin = factory.createAdmin();

        System.out.println("Wait for GSA");
        boolean discoverGSA = admin.getGridServiceAgents().waitFor(1,30, TimeUnit.SECONDS) ;
        System.out.println("Discovered GSA:" + discoverGSA);
        if (!discoverGSA) {
            System.out.println("Fail to find GSA");
            System.exit(1);
        }
        GridServiceAgent gsa = admin.getGridServiceAgents().getAgents()[0];
        GridServiceContainers gscs = admin.getGridServiceContainers();
        gscs.waitFor(4, 2, TimeUnit.SECONDS) ;
        int numberOfGSCs = gscs.getSize();
        int times=0;
        while ( times < 20 ) {
            times++;
            System.out.println("******************************************");
            System.out.println("Running " + times + " time out of 20");
            GridServiceContainer[] containers = gscs.getContainers();
            boolean extensiveHeapSize=false;
            boolean relocated=false;
            for (int cnt=0; cnt < containers.length ; cnt++) {
                System.out.println("containers " + (cnt+1) + " UID is " + containers[cnt].getUid());
                double heapSize = containers[cnt].getVirtualMachine().getStatistics().getMemoryHeapUsedInMB();
                System.out.println("Used Heap Size: " + heapSize  );
                if (heapSize > 50 ) {
                    System.out.println("try to relocate PU instance to release some memory" );
                    containers[cnt].waitFor(1,4,TimeUnit.SECONDS);
                    ProcessingUnitInstance[] puis = containers[cnt].getProcessingUnitInstances();
                    if (puis.length > 1) {
                        extensiveHeapSize=true;
                        relocated=false;
                        System.out.println("These is more than 1 PU in this GSC Look for Another Candidate GSC for relocation of the PU. " + puis.length);
                        for (int lookup=0; lookup < containers.length ; lookup++) {
                            containers[lookup].waitFor(1,4,TimeUnit.SECONDS);
                            ProcessingUnitInstance[] mypuis = containers[lookup].getProcessingUnitInstances();
                            if (mypuis.length == 0) {
                                System.out.println("Found Candidate GSC Container for Relocation. Attempting to Relocate first PU to move it to another GSC" );
                                puis[0].relocate( containers[lookup] );
                                System.out.println("PU instance was relocated successfully.");
                                relocated=true;
                                break;
                            }
                        }
                    }
                }
            }
            if (extensiveHeapSize) {
                if (!relocated) {
                    System.out.println("No Available GSC were found for relocation, start a new one.");
                    GridServiceContainerOptions gscOptions = new GridServiceContainerOptions();
                    gscOptions.vmInputArgument("-Dcom.gs.zones=AdminApiZone");
                    gscOptions.vmInputArgument("-Xmx100m");
                    gscOptions.vmInputArgument("-Xms100m");
                    gsa.startGridService(gscOptions);
                    System.out.println("New GSC is being started.");
                }
                extensiveHeapSize=false;
            } else {
                System.out.println("Current system state does not require new GSC");
            }
            System.out.println("******************************************");
            Thread.sleep(delay);
        }

        admin.close();


    }
}
