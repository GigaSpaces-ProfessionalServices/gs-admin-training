package com.gs.admin.alerts;


/**
 * UserFeederActivator class create a standalone proxy connection to the space using configurer.
 * The class then activates the UserFeeder to write all user into the space.
 *
 * @author Jay Dalal
 */

public class MonitorStarter {

    public static void main(String[] args) {
        new AlertsMonitor();
    }


}
