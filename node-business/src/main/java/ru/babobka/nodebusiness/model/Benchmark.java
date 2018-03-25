package ru.babobka.nodebusiness.model;

/**
 * Created by 123 on 15.03.2018.
 */
public class Benchmark {
    private String id;
    private long executionTime;
    private long startTime;
    private String description;
    private String appName;
    private int slaves;
    private int serviceThreads;
    private String os;
    private String user;
    private int processors;
    private long ramBytes;
    private String javaVersion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSlaves() {
        return slaves;
    }

    public void setSlaves(int slaves) {
        this.slaves = slaves;
    }

    public int getServiceThreads() {
        return serviceThreads;
    }

    public void setServiceThreads(int serviceThreads) {
        this.serviceThreads = serviceThreads;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }

    public long getRamBytes() {
        return ramBytes;
    }

    public void setRamBytes(long ramBytes) {
        this.ramBytes = ramBytes;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public void setCurrentSystemInfo() {
        setOs(System.getProperty("os.name"));
        setJavaVersion(System.getProperty("java.version"));
        setUser(System.getProperty("user.name"));
        setRamBytes(Runtime.getRuntime().maxMemory());
        setProcessors(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public String toString() {
        return "Benchmark{" +
                "id='" + id + '\'' +
                ", executionTime=" + executionTime +
                ", startTime=" + startTime +
                ", description='" + description + '\'' +
                ", appName='" + appName + '\'' +
                ", slaves=" + slaves +
                ", serviceThreads=" + serviceThreads +
                ", os='" + os + '\'' +
                ", user='" + user + '\'' +
                ", processors=" + processors +
                ", ramBytes=" + ramBytes +
                ", javaVersion='" + javaVersion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Benchmark benchmark = (Benchmark) o;

        if (executionTime != benchmark.executionTime) return false;
        if (startTime != benchmark.startTime) return false;
        if (slaves != benchmark.slaves) return false;
        if (serviceThreads != benchmark.serviceThreads) return false;
        if (processors != benchmark.processors) return false;
        if (ramBytes != benchmark.ramBytes) return false;
        if (id != null ? !id.equals(benchmark.id) : benchmark.id != null) return false;
        if (description != null ? !description.equals(benchmark.description) : benchmark.description != null)
            return false;
        if (appName != null ? !appName.equals(benchmark.appName) : benchmark.appName != null) return false;
        if (os != null ? !os.equals(benchmark.os) : benchmark.os != null) return false;
        if (user != null ? !user.equals(benchmark.user) : benchmark.user != null) return false;
        return javaVersion != null ? javaVersion.equals(benchmark.javaVersion) : benchmark.javaVersion == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (executionTime ^ (executionTime >>> 32));
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + slaves;
        result = 31 * result + serviceThreads;
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + processors;
        result = 31 * result + (int) (ramBytes ^ (ramBytes >>> 32));
        result = 31 * result + (javaVersion != null ? javaVersion.hashCode() : 0);
        return result;
    }
}
