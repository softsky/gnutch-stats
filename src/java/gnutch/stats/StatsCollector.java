package gnutch.stats;

import java.util.Map;
import java.util.HashMap;
import org.apache.camel.Exchange;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StatsCollector {
    private Map<String, AtomicLong> statistic = new HashMap<String, AtomicLong>();
    private Map<String, AtomicLongArray> arrayStatistic = new HashMap<String, AtomicLongArray>();

    protected Scheduler quartzScheduler;
    protected Integer statisticTimeoutMsec = 15000; // 15 seconds for statistic gathering

    protected static class MyJob implements Job {
	public void execute(JobExecutionContext context) throws JobExecutionException {
	    // Say Hello to the World and display the date/time
	    _log.info("Hello World! - " + new Date());
	}
    }

    public void init(){
	if(quartzScheduler.isStarted() == false)
	    quartzScheduler.start();


	JobDetail job = quartzScheduler.newJob(StatsCollector.MyJob.class)
	    .withIdentity("job1", "group1")
	    .build();
	// Define a Trigger that will fire "now"
	//Trigger trigger = new SimpleTrigger("trigger1", "group1", new Date());
	// Or this one will repeat 10 times after 100 ms.
	Trigger trigger = new SimpleTrigger("trigger1", "group1", 0, statisticTimeoutMsec);

	// Schedule the job with the trigger
	quartzScheduler.scheduleJob(job, trigger);	
    }

    public void collect(Exchange ex) {
        String from = ex.getIn().getHeader("statsFrom").toString();
 
        synchronized(statistic){
            if(statistic.containsKey(from) == false)
                statistic.put(from, new AtomicLong());
            
            statistic.get(from).incrementAndGet();
        }

        // TODO; complete implementation
        // so, basically what I need:
        // a) arrayStatistic should be filled with values. 
        // Every new value is added to arrayStatistic if statisticTimeoutMsec timeout elapsed since pervious value been added to arrayStatistic
        // basically what this gives to us: #collect() method is called many times from different threads, it just increase values of statistic map
        // for particular statsFrom header. Every 15 seconds (statisticTimeoutMsec value)
        // we should save the value of statistic[index] into arrayStatistic[index] and reset statistic[index] to zero.
        // if we do so, in arrayStatistic[index] we will have amounts of values for particular `index` gathered for 15 seconds (statisticTimeoutMsec value)
    }


    public Map<String, AtomicLong> getStatistic(){
        return statistic;
    }

    public Map<String, AtomicLongArray> getArrayStatistic(){
        return arrayStatistic;
    }
}


