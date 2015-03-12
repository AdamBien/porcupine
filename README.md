# porcupine
Configurable threading, resiliency and monitoring with injectable statistics for Java EE 7

Features:

1. Conventional: ExecutorService is directly injectable. The thread pool derives the name from the field, but can be easily overridden.
2. Drop-in install: a single dependency in the pom.xml is sufficient. 
3. Standard based: porcupine uses [JSR 236: Concurrency Utilities for JavaTM EE](https://www.jcp.org/en/jsr/detail?id=236)
4. Small: the entire framework is: 16kB.
5. Extensible without configuration: All major components can be replaced (big thanks to [@Specializes](http://docs.oracle.com/javaee/7/api/javax/enterprise/inject/Specializes.html))

### Booting Porcupine
[![Intro](https://i.ytimg.com/vi/20KVZ0ZnCl4/mqdefault.jpg)](https://www.youtube.com/embed/20KVZ0ZnCl4?rel=0)

### Configuration
[![Configuration](https://i.ytimg.com/vi/4M1EJntwjk8/mqdefault.jpg)](https://www.youtube.com/embed/4M1EJntwjk8?rel=0)
### Dealing with Overload
[![Configuration](https://i.ytimg.com/vi/HToJuV28pPI/mqdefault.jpg)](https://www.youtube.com/embed/HToJuV28pPI?rel=0)
##Installation

```
        <dependency>
            <groupId>com.airhacks</groupId>
            <artifactId>porcupine</artifactId>
            <version>NEWEST_VERSION</version>
            <scope>compile</scope>
        </dependency>
```

##Conventional Usage

```
@Stateless
public class MessagesService {

    @Inject
    @Dedicated
    ExecutorService light;

    @Inject
    @Dedicated
    ExecutorService heavy;
```

Custom naming is also supported:
```
    @Inject
    @Dedicated("custom-pool")
    private ExecutorService first;
```

##Statistics and monitoring

###Exposure

	@Path("statistics")
	@RequestScoped
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public class StatisticsResource {

	    @Inject
	    Instance<List<Statistics>> statistics;

    	@GET
	    public List<Statistics> expose() {
	        return this.statistics.get();
    	}

	}
	
###Sample output

Unified thread pool statistics are accessible via injection. Individual statistics are injectable with the `@Dedicated` qualifier.

XML statistics

```
<statistics>
	<pipelineName>light</pipelineName>
	<remainingQueueCapacity>100</remainingQueueCapacity>
	<completedTaskCount>1</completedTaskCount>
	<activeThreadCount>0</activeThreadCount>
	<largestThreadPoolSize>1</largestThreadPoolSize>
	<currentThreadPoolSize>1</currentThreadPoolSize>
	<totalNumberOfTasks>1</totalNumberOfTasks>
	<maximumPoolSize>48</maximumPoolSize>
	<rejectedExecutionHandlerName>ExecutorServiceExposer$$Lambda$3/1562863014</rejectedExecutionHandlerName>
	<rejectedTasks>0</rejectedTasks>
</statistics>
<statistics>
	<pipelineName>heavy</pipelineName>
	<remainingQueueCapacity>16</remainingQueueCapacity>
	<completedTaskCount>0</completedTaskCount>
	<activeThreadCount>1</activeThreadCount>
	<largestThreadPoolSize>1</largestThreadPoolSize>
	<currentThreadPoolSize>1</currentThreadPoolSize>
	<totalNumberOfTasks>1</totalNumberOfTasks>
	<maximumPoolSize>8</maximumPoolSize>
	<rejectedExecutionHandlerName>CallerRunsPolicy</rejectedExecutionHandlerName>
	<rejectedTasks>0</rejectedTasks>
</statistics>
```
JSON statistics
```
[{
        "pipelineName": "light",
        "remainingQueueCapacity": 100,
        "completedTaskCount": 1,
        "activeThreadCount": 0,
        "largestThreadPoolSize": 1,
        "currentThreadPoolSize": 1,
        "totalNumberOfTasks": 1,
        "maximumPoolSize": 48,
        "rejectedExecutionHandlerName": "ExecutorServiceExposer$$Lambda$3/1562863014",
        "rejectedTasks": 0
    },
    {
        "pipelineName": "heavy",
        "remainingQueueCapacity": 16,
        "completedTaskCount": 1,
        "activeThreadCount": 0,
        "largestThreadPoolSize": 1,
        "currentThreadPoolSize": 1,
        "totalNumberOfTasks": 1,
        "maximumPoolSize": 8,
        "rejectedExecutionHandlerName": "CallerRunsPolicy",
        "rejectedTasks": 0
    }]
```
## Custom configuration (fully optional)

```
@Specializes
public class CustomExecutorConfigurator extends ExecutorConfigurator {

    @Override
    public ExecutorConfiguration defaultConfigurator() {
        return super.defaultConfigurator();
    }

    @Override
    public ExecutorConfiguration forPipeline(String name) {
        if ("heavy".equals(name)) {
            return new ExecutorConfiguration.Builder().
                    corePoolSize(4).
                    maxPoolSize(8).
                    queueCapacity(16).
                    keepAliveTime(1).
                    callerRunsPolicy().
                    build();
        }
        return super.forPipeline(name);
    }

}
```
