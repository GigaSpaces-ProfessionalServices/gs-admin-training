# gs-admin-training - lab07-metrics

# GigaSpaces Metrics

## Lab Goals

 * Explore the GigaSpaces metrics framework.
 * Get familiar with Grafana and InfluxDB.

## Lab Description
In this lab we will focus on GigaSpaces Metrics functionalities.  
To better know its capabilities you will setup InfluxDB and Grafana.

### 1 Download and install InfluxDB and Grafana

#### 1.1 Choose for your convenience, 1 of the following 2 options:

* InfluxDB regular installation:  
  https://www.influxdata.com/get-influxdb/

* InfluxDB docker deployment:  
  https://hub.docker.com/_/influxdb

#### 1.2 Create mydb database

* Follow the instructions to create **mydb** database:  
  https://docs.influxdata.com/influxdb/v1.3/introduction/getting_started/

**Tip:** For docker please use the following command first:<br>
`docker exec -it <container ID> /bin/bash`

### 2 Download and install Grafana

#### 2.1 Choose for your convenient 1 of the following 2 options:

* Grafana regular installation:  
  https://grafana.com/get

* Grafana docker deployment:  
  https://hub.docker.com/r/grafana/grafana

#### 2.2 Verify Grafana is up and running

* Click the following link:  
  http://localhost:3000/

See that you can login admin/admin (skip the change password page).

### 3 Activate GS metrics

* Please follow instructions at:  
  https://docs.gigaspaces.com/latest/admin/web-management-monitoring.html#InstallingandConfiguringGrafana

### 4 Run GS in Demo mode
```
$GS_HOME/bin/gs.sh demo
```    
### 5 Explore Grafana dashboards

Please explore Grafana GS dashboard and see that metrics are arriving:  
https://docs.gigaspaces.com/latest/admin/web-management-monitoring.html#GettingStarted

### 6 Troubleshooting:

* Sometimes there is a need to change access from server (default) to Browser.
  
![Screenshot](./Pictures/Picture1.png)

* InfluxDB connectivity test can be done:
![Screenshot](./Pictures/Picture2.png)

* When using docker sometime Grafana can't be reached by the gs-webui.
![Screenshot](./Pictures/Picture3.png)

To overcome the above please use the direct link:  
http://localhost:3000/

### 7 Space dashboard:

Please use the GS benchmark tool as you did in lab04 and explore the Space-demo dashboard:
![Screenshot](./Pictures/Picture4.png)

