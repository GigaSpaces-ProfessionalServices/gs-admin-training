package com.gs.admin.feeder;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

public class Feeder {
    public static void main(String[] args) throws InterruptedException {
        // Create a space proxy
        final String txt = "Some text to write ";
        System.out.println("Running Feeder");
        GigaSpace gigaSpace = new GigaSpaceConfigurer( new SpaceProxyConfigurer("demo").lookupGroups(System.getenv("GS_LOOKUP_GROUPS"))).gigaSpace();
        for (int k=0; k <100000; k++){
            Data data = new Data();
            data.setId(k);
            data.setTxt1(txt + k);
            data.setTxt3(txt + k);
            gigaSpace.write(data);
            if (k%1000 == 0){
                Thread.sleep(100);;
            }
        }
        System.out.println("Feeder is done");
    }
}
