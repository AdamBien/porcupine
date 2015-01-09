package com.airhacks.porcupine.execution.control;

import com.airhacks.porcupine.execution.entity.Rejection;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 *
 * @author airhacks.com
 */
@Singleton
public class ExecutorExposer {

    private ThreadPoolExecutor threadPoolExecutor = null;
    private BlockingQueue<Runnable> queue;
    private AtomicLong rejectedTasks;

    @Resource
    ManagedThreadFactory threadFactory;

    private final int CORE_POOL_SIZE = 1;
    private final int MAX_POOL_SIZE = 2;
    private final int KEEP_ALIVE_TIME = 1;

    private RejectedExecutionHandler rejectedExecutionHandler;

    @Inject
    Event<Rejection> rejections;

    @PostConstruct
    public void postConstruct() {
        this.queue = new ArrayBlockingQueue<>(10);
        this.rejectedTasks = new AtomicLong();
        this.rejectedExecutionHandler = this::onRejectedExecution;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, this.queue, threadFactory,
                this.rejectedExecutionHandler);
    }

    public void onRejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        this.rejectedTasks.incrementAndGet();
        this.rejections.fire(new Rejection(getStatistics(), r.getClass().getName()));
    }

    @Produces
    @Managed
    public ExecutorService expose() {
        return threadPoolExecutor;
    }

    @Produces
    public Statistics getStatistics() {
        int remainingQueueCapacity = this.queue.remainingCapacity();
        long completedTaskCount = this.threadPoolExecutor.getCompletedTaskCount();
        int activeThreadCount = this.threadPoolExecutor.getActiveCount();
        int largestThreadPoolSize = this.threadPoolExecutor.getLargestPoolSize();
        int currentThreadPoolSize = this.threadPoolExecutor.getPoolSize();
        long totalNumberOfTasks = this.threadPoolExecutor.getTaskCount();
        int maximumPoolSize = this.threadPoolExecutor.getMaximumPoolSize();

        return new Statistics(remainingQueueCapacity, completedTaskCount, activeThreadCount, largestThreadPoolSize, currentThreadPoolSize, totalNumberOfTasks, maximumPoolSize, this.rejectedTasks.get());
    }

    @PreDestroy
    public void shutdown() {
        List<Runnable> unprocessedTasked = this.threadPoolExecutor.shutdownNow();
        System.out.println("Shutting down, unprocessed tasks:");
        unprocessedTasked.forEach(System.out::println);
    }

}
