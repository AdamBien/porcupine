package com.airhacks.porcupine.execution.entity;

import com.airhacks.porcupine.execution.boundary.Dedicated;
import java.util.concurrent.ThreadPoolExecutor;
import javax.enterprise.inject.Alternative;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A DTO grouping relevant statistic information. All attributes are directly
 * derived from {@link ThreadPoolExecutor}.
 *
 * The {@link Statistics#pipelineName} attribute is the name used in the
 * {@link Dedicated} qualifier.
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
    private String rejectedExecutionHandlerName;
    private long rejectedTasks;

    public Statistics(String pipelineName, int remainingQueueCapacity, long completedTaskCount, int activeThreadCount, int largestThreadPoolSize, int currentThreadPoolSize, long totalNumberOfTasks, int maximumPoolSize, String rejectedExecutionHandlerName, long rejectedTasks) {
        this.pipelineName = pipelineName;
        this.remainingQueueCapacity = remainingQueueCapacity;
        this.completedTaskCount = completedTaskCount;
        this.activeThreadCount = activeThreadCount;
        this.largestThreadPoolSize = largestThreadPoolSize;
        this.currentThreadPoolSize = currentThreadPoolSize;
        this.totalNumberOfTasks = totalNumberOfTasks;
        this.maximumPoolSize = maximumPoolSize;
        this.rejectedTasks = rejectedTasks;
        this.rejectedExecutionHandlerName = rejectedExecutionHandlerName;
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

    public String getRejectedExecutionHandlerName() {
        return rejectedExecutionHandlerName;
    }

    @Override
    public String toString() {
        return "Statistics{" + "pipelineName=" + pipelineName + ", remainingQueueCapacity=" + remainingQueueCapacity + ", completedTaskCount=" + completedTaskCount + ", activeThreadCount=" + activeThreadCount + ", largestThreadPoolSize=" + largestThreadPoolSize + ", currentThreadPoolSize=" + currentThreadPoolSize + ", totalNumberOfTasks=" + totalNumberOfTasks + ", maximumPoolSize=" + maximumPoolSize + ", rejectedExecutionHandlerName=" + rejectedExecutionHandlerName + ", rejectedTasks=" + rejectedTasks + '}';
    }

}
