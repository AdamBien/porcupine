# porcupine
Configurable threading, resiliency and monitoring with injectable statistics for Java EE 7. Porcupine is the implementation of the [bulkhead](https://johnragan.wordpress.com/2009/12/08/release-it-stability-patterns-and-best-practices/) and [handshaking](https://johnragan.wordpress.com/2009/12/08/release-it-stability-patterns-and-best-practices/) patterns for Java EE 7.

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
[![Overload](https://i.ytimg.com/vi/HToJuV28pPI/mqdefault.jpg)](https://www.youtube.com/embed/HToJuV28pPI?rel=0)

### Automatic Statistics Injection
[![Overload](https://i.ytimg.com/vi/0slZph_LC0A/mqdefault.jpg)](https://www.youtube.com/embed/0slZph_LC0A?rel=0)

##Installation

```
        <dependency>
            <groupId>com.airhacks</groupId>
            <artifactId>porcupine</artifactId>
            <version>NEWEST_VERSION</version>
            <scope>compile</scope>
        </dependency>
```
With statistics injection into HTTP headers of all JAX-RS resources:

```
        <dependency>
            <groupId>com.airhacks</groupId>
            <artifactId>porcupine-spy</artifactId>
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

## Statistics Injection into HTTP Headers

```
HTTP/1.1 200 OK
Server: GlassFish Server Open Source Edition  4.1 
X-Powered-By: Servlet/3.1 JSP/2.3 (GlassFish Server Open Source Edition  4.1  Java/Oracle Corporation/1.8)
```
```json
x-porcupine-statistics-light: {"pipelineName":"light","activeThreadCount":1,"completedTaskCount":1,
"corePoolSize":8,"currentThreadPoolSize":2,"largestThreadPoolSize":2,"maximumPoolSize":16,"rejectedTasks":0,
"remainingQueueCapacity":100,"minQueueCapacity":100,"totalNumberOfTasks":2}
x-porcupine-statistics-heavy: {"pipelineName":"heavy","rejectedExecutionHandlerName":"CallerRunsPolicy",
"activeThreadCount":0,"completedTaskCount":1,"corePoolSize":4,"currentThreadPoolSize":1,
"largestThreadPoolSize":1,"maximumPoolSize":8,"rejectedTasks":0,"remainingQueueCapacity":16,
"minQueueCapacity":16,"totalNumberOfTasks":1}
```
```
Content-Type: text/plain
Date: Wed, 18 Mar 2015 07:50:20 GMT
Content-Length: 36
```


