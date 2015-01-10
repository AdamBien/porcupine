package com.airhacks.porcupine.execution.control;

import com.airhacks.porcupine.execution.entity.Pipeline;
import com.airhacks.porcupine.execution.entity.Rejection;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 *
 * @author airhacks.com
 */
public class ExecutorServiceExposer {

    @Inject
    ManagedThreadFactory threadFactory;

    @Inject
    Event<Rejection> rejections;

    @Inject
    PipelineStore ps;

    public void onRejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Pipeline pipeline = findPipeline(executor);
        pipeline.taskRejected();
        this.rejections.fire(new Rejection(pipeline.getStatistics(), r.getClass().getName()));
    }

    @Produces
    @Managed
    public ExecutorService exposeExecutorService(InjectionPoint ip) {
        String fieldName = ip.getMember().getName();
        Annotated annotated = ip.getAnnotated();
        Managed annotation = annotated.getAnnotation(Managed.class);
        return createFromAnnotation(fieldName, annotation);
    }

    ExecutorService createFromAnnotation(String fieldName, Managed annotation) {
        int corePoolSize = annotation.corePoolSize();
        int keepAliveTime = annotation.keepAliveTime();
        int maxPoolSize = annotation.maxPoolSize();
        int queueCapacity = annotation.queueCapacity();
        String pipelineName = calculateName(fieldName, annotation);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
        RejectedExecutionHandler rejectedExecutionHandler = this::onRejectedExecution;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler);
        this.ps.putIfAbsent(pipelineName, new Pipeline(pipelineName, threadPoolExecutor));
        return threadPoolExecutor;
    }

    String calculateName(String fieldName, Managed annotation) {
        final String nameFromAnnotation = annotation.pipelineName();
        if (Managed.UNSET.equalsIgnoreCase(nameFromAnnotation)) {
            return fieldName;
        } else {
            return nameFromAnnotation;
        }
    }

    @Produces
    public Statistics exposeStatistics(InjectionPoint ip) {
        String name = ip.getMember().getName();
        return getStatistics(name);
    }

    public Statistics getStatistics(String name) {
        Pipeline pipeline = this.ps.get(name);
        if (pipeline != null) {
            return pipeline.getStatistics();
        } else {
            return new Statistics();
        }
    }

    @PreDestroy
    public void shutdown() {
        this.ps.pipelines().parallelStream().forEach(p -> p.shutdown());
    }

    Pipeline findPipeline(ThreadPoolExecutor executor) {
        return this.ps.pipelines().stream().filter((p) -> p.manages(executor)).findFirst().orElse(null);
    }

}
