# porcupine
Threading, Resiliency and Monitoring for Java EE 7

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

##Statistics and monitoring

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
	
	
##Configuration

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