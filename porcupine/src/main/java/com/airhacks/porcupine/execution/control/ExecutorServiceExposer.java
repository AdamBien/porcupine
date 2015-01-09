package com.airhacks.porcupine.execution.control;

import com.airhacks.porcupine.execution.entity.Pipeline;
import com.airhacks.porcupine.execution.entity.Rejection;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 *
 * @author airhacks.com
 */
@ApplicationScoped
public class ExecutorServiceExposer {

    @Inject
    ManagedThreadFactory threadFactory;

    @Inject
    Event<Rejection> rejections;

    ConcurrentHashMap<String, Pipeline> pipelines;

    @PostConstruct
    public void init() {
        this.pipelines = new ConcurrentHashMap<>();
    }

    public void onRejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Pipeline pipeline = findPipeline(executor);
        pipeline.taskRejected();
        this.rejections.fire(new Rejection(pipeline.getStatistics(), r.getClass().getName()));
    }

    @Produces
    @Managed
    public ExecutorService expose(InjectionPoint ip) {
        Annotated annotated = ip.getAnnotated();
        Managed annotation = annotated.getAnnotation(Managed.class);
        return createFromAnnotation(annotation);
    }

    ExecutorService createFromAnnotation(Managed annotation) {
        int corePoolSize = annotation.corePoolSize();
        int keepAliveTime = annotation.keepAliveTime();
        int maxPoolSize = annotation.maxPoolSize();
        int queueCapacity = annotation.queueCapacity();
        String pipelineName = annotation.pipelineName();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
        RejectedExecutionHandler rejectedExecutionHandler = this::onRejectedExecution;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler);
        this.pipelines.putIfAbsent(pipelineName, new Pipeline(pipelineName, threadPoolExecutor));
        return threadPoolExecutor;
    }

    @Produces
    public Statistics getStatistics(InjectionPoint ip) {
        String name = ip.getMember().getName();
        Pipeline pipeline = this.pipelines.get(name);
        if (pipeline != null) {
            return pipeline.getStatistics();
        } else {
            return new Statistics();
        }

    }

    @PreDestroy
    public void shutdown() {
        this.pipelines.values().parallelStream().forEach(p -> p.shutdown());
    }

    Pipeline findPipeline(ThreadPoolExecutor executor) {
        return this.pipelines.values().stream().filter((p) -> p.manages(executor)).findFirst().orElse(null);
    }

}
