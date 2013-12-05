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

    // TODO: rename MyJob into StatsCollectorJob
    protected static class MyJob implements Job {
	public void execute(JobExecutionContext context) throws JobExecutionException {
            // TODO: finish implementation:
            // a) synchronize statistic and arrayStatistic
            // b) store values from statistic to arrayStatistic with appropriate map keys
            // c) if statistic has zero value for appropriate key, store zero into arrayStatistic
	}
    }

    public void init(){
	if(quartzScheduler.isStarted() == false)
	    quartzScheduler.start();


	JobDetail job = quartzScheduler.newJob(StatsCollector.MyJob.class)
            // TODO: rename job and group
            // group should be named: `statsCollectorGroup`
            // trigger should be named `collectJob`            
	    .withIdentity("job1", "group1")
	    .build();
        // TODO rename trigger and group
        // group should be named: `statsCollectorGroup`
        // trigger should be named `collectTrigger`
	Trigger trigger = new SimpleTrigger("trigger1", "group1", 0, statisticTimeoutMsec);

	// Schedule the job with the trigger
	quartzScheduler.scheduleJob(job, trigger);	
    }

    /**
     * Called from Camel Routes every time message leaves some producer
     * just increase `statistic` counter for that particular producer
     *
     * @param ex - Camel exchange
     */
    public void collect(Exchange ex) {
        String from = ex.getIn().getHeader("statsFrom").toString();
 
        synchronized(statistic){
            if(statistic.containsKey(from) == false)
                statistic.put(from, new AtomicLong());
            
            statistic.get(from).incrementAndGet();
        }
    }


    /**
     * Getter for `statistic` field
     *
     * @return value of `statistic` field
     */
    public Map<String, AtomicLong> getStatistic(){
        return statistic;
    }

    /**
     * Getter for `arrayStatistic` field
     *
     * @return value of `arrayStatistic` field
     */
    public Map<String, AtomicLongArray> getArrayStatistic(){
        return arrayStatistic;
    }
}


