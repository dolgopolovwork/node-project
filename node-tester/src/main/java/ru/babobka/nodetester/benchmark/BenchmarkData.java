package ru.babobka.nodetester.benchmark;

/**
 * Created by 123 on 18.03.2018.
 */
public class BenchmarkData {
    private final String description;
    private final long executionTime;

    public BenchmarkData(String description, long executionTime) {
        this.description = description;
        this.executionTime = executionTime;
    }

    public String getDescription() {
        return description;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public String toString() {
        return "BenchmarkData{" +
                "description='" + description + '\'' +
                ", executionTime=" + executionTime +
                '}';
    }
}
