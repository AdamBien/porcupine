package com.airhacks.porcupine.execution.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airhacks.com
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Rejection {

    private Statistics statistics;
    private String taskClass;

    public Rejection(Statistics statistics, String taskClass) {
        this.statistics = statistics;
        this.taskClass = taskClass;
    }

    public Rejection() {
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public String getTaskClass() {
        return taskClass;
    }

}
