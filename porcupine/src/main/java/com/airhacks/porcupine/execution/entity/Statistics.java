package com.airhacks.porcupine.execution.entity;

import javax.enterprise.inject.Alternative;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airhacks.com
 */
@Alternative
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Statistics {

    private String pipelineName;
    private int remainingQueueCapacity;
    private long completedTaskCount;
    private int activeThreadCount;
    private int largestThreadPoolSize;
    private int currentThreadPoolSize;
    private long totalNumberOfTasks;
    private int maximumPoolSize;
    private long rejectedTasks;

    public Statistics(String pipelineName, int remainingQueueCapacity, long completedTaskCount, int activeThreadCount, int largestThreadPoolSize, int currentThreadPoolSize, long totalNumberOfTasks, int maximumPoolSize, long rejectedTasks) {
        this.pipelineName = pipelineName;
        this.remainingQueueCapacity = remainingQueueCapacity;
        this.completedTaskCount = completedTaskCount;
        this.activeThreadCount = activeThreadCount;
        this.largestThreadPoolSize = largestThreadPoolSize;
        this.currentThreadPoolSize = currentThreadPoolSize;
        this.totalNumberOfTasks = totalNumberOfTasks;
        this.maximumPoolSize = maximumPoolSize;
        this.rejectedTasks = rejectedTasks;
    }

    public Statistics() {
    }

    public int getRemainingQueueCapacity() {
        return remainingQueueCapacity;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public int getActiveThreadCount() {
        return activeThreadCount;
    }

    public int getLargestThreadPoolSize() {
        return largestThreadPoolSize;
    }

    public int getCurrentThreadPoolSize() {
        return currentThreadPoolSize;
    }

    public long getTotalNumberOfTasks() {
        return totalNumberOfTasks;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getRejectedTasks() {
        return rejectedTasks;
    }

    public String getPipelineName() {
        return pipelineName;
    }

}
