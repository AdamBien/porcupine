package com.airhacks.porcupine.execution.boundary;

import com.airhacks.porcupine.configuration.control.ExecutorConfigurator;
import com.airhacks.porcupine.execution.control.ExecutorConfiguration;
import com.airhacks.porcupine.execution.control.PipelineStore;
import com.airhacks.porcupine.execution.entity.Pipeline;
import com.airhacks.porcupine.execution.entity.Rejection;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    ThreadFactory threadFactory;

    @Inject
    Event<Rejection> rejections;

    @Inject
    PipelineStore ps;

    @Inject
    ExecutorConfigurator ec;

    public void onRejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Pipeline pipeline = findPipeline(executor);
        pipeline.taskRejected();
        this.rejections.fire(new Rejection(pipeline.getStatistics(), r.getClass().getName()));
    }

    @Produces
    @Dedicated
    public ExecutorService exposeExecutorService(InjectionPoint ip) {
        String pipelineName = getPipelineName(ip);
        final Pipeline existingPipeline = this.ps.get(pipelineName);
        if (existingPipeline != null) {
            return existingPipeline.getExecutor();
        }
        ExecutorConfiguration config = this.ec.forPipeline(pipelineName);
        RejectedExecutionHandler rejectedExecutionHandler = config.getRejectedExecutionHandler();
        if (rejectedExecutionHandler == null) {
            rejectedExecutionHandler = this::onRejectedExecution;
        }
        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(config, rejectedExecutionHandler);
        this.ps.put(pipelineName, new Pipeline(pipelineName, threadPoolExecutor));
        return threadPoolExecutor;
    }

    ThreadPoolExecutor createThreadPoolExecutor(ExecutorConfiguration config, RejectedExecutionHandler rejectedExecutionHandler) {
        int corePoolSize = config.getCorePoolSize();
        int keepAliveTime = config.getKeepAliveTime();
        int maxPoolSize = config.getMaxPoolSize();
        int queueCapacity = config.getQueueCapacity();
        BlockingQueue<Runnable> queue;
        if (queueCapacity > 0) {
            queue = new ArrayBlockingQueue<>(queueCapacity);
        } else {
            queue = new SynchronousQueue<>();
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, queue, threadFactory,
                rejectedExecutionHandler);
        return threadPoolExecutor;
    }

    @Produces
    @Dedicated
    public Statistics exposeStatistics(InjectionPoint ip) {
        String name = getPipelineName(ip);
        return this.ps.getStatistics(name);
    }

    @Produces
    public List<Statistics> getAllStatistics() {
        return this.ps.getAllStatistics();
    }

    String getPipelineName(InjectionPoint ip) {
        Annotated annotated = ip.getAnnotated();
        Dedicated dedicated = annotated.getAnnotation(Dedicated.class);
        String name;
        if (dedicated != null && !Dedicated.DEFAULT.equals(dedicated.value())) {
            name = dedicated.value();
        } else {
            name = ip.getMember().getName();
        }
        return name;
    }

    Pipeline findPipeline(ThreadPoolExecutor executor) {
        return this.ps.pipelines().stream().filter((p) -> p.manages(executor)).findFirst().orElse(null);
    }
}
