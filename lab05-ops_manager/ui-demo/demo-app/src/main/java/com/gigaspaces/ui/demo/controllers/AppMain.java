package com.gigaspaces.ui.demo.controllers;


import java.io.IOException;

public class AppMain {
    public static void main (String args[]) {
        DemoController controller = new DemoController();
        /*try {
            System.out.println(controller.markInstanceUnavailable("ProductsCatalog","ProductsCatalog~2_2", 30 ));
        } catch (ApiException e) {
            e.printStackTrace();
        }*/
        /*try {
            System.out.println(controller.markInstanceUnavailable("Mirror","Mirror~1", 30 ));
        } catch (ApiException e) {
            e.printStackTrace();
        }*/


        //System.out.println(controller.triggerCPUAlertOnService("ProductsCatalog", 60));


        System.out.println(controller.triggerMemoryAlertOnService("ProductsCatalog", 60));

        controller.close();
    }
}
