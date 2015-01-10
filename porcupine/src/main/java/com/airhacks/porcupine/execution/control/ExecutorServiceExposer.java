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
    ExecutorService exposeExecutorService(InjectionPoint ip) {
        Annotated annotated = ip.getAnnotated();
        Managed annotation = annotated.getAnnotation(Managed.class);

        int corePoolSize = annotation.corePoolSize();
        int keepAliveTime = annotation.keepAliveTime();
        int maxPoolSize = annotation.maxPoolSize();
        int queueCapacity = annotation.queueCapacity();
        String pipelineName = getPipelineName(ip);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
        RejectedExecutionHandler rejectedExecutionHandler = this::onRejectedExecution;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler);
        this.ps.putIfAbsent(pipelineName, new Pipeline(pipelineName, threadPoolExecutor));
        return threadPoolExecutor;
    }

    @Produces
    public Statistics exposeStatistics(InjectionPoint ip) {
        String name = getPipelineName(ip);
        return this.ps.getStatistics(name);
    }

    String getPipelineName(InjectionPoint ip) {
        Annotated annotated = ip.getAnnotated();
        Dedicated dedicated = annotated.getAnnotation(Dedicated.class);
        String name;
        if (dedicated != null) {
            name = dedicated.value();
        } else {
            name = ip.getMember().getName();
        }
        return name;
    }

    @PreDestroy
    public void shutdown() {
        this.ps.pipelines().parallelStream().forEach(p -> p.shutdown());
    }

    Pipeline findPipeline(ThreadPoolExecutor executor) {
        return this.ps.pipelines().stream().filter((p) -> p.manages(executor)).findFirst().orElse(null);
    }

}
