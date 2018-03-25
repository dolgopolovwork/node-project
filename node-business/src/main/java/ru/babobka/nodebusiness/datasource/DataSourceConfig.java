package ru.babobka.nodebusiness.datasource;

import java.io.Serializable;

/**
 * Created by 123 on 18.03.2018.
 */
public class DataSourceConfig implements Serializable {
    private static final long serialVersionUID = -1807923264776355064L;
    private String driverName;
    private String connectURI;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getConnectURI() {
        return connectURI;
    }

    public void setConnectURI(String connectURI) {
        this.connectURI = connectURI;
    }
}
