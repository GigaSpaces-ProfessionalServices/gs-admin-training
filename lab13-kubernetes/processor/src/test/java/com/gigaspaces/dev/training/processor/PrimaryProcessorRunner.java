package com.gigaspaces.dev.training.processor;

import org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainer;

public class PrimaryProcessorRunner {

    public static void main(String[] args) throws Exception {
        IntegratedProcessingUnitContainer.main(new String[]{"-cluster", "schema=primary_backup", "total_members=1,1", "id=1"});
    }
}
