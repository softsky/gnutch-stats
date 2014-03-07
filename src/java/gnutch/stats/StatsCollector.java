package gnutch.stats;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.camel.Exchange;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsCollector {
    private static Logger LOG = LoggerFactory.getLogger(StatsCollector.class);

    private Map<String, AtomicLong> statistic = new HashMap<String, AtomicLong>();
    private Map<String, List<List<Long>>> arrayStatistic = new HashMap<String, List<List<Long>>>();

    protected Scheduler quartzScheduler;
    protected Integer statisticTimeoutMsec = 15000; // 15 seconds for statistic gathering

    public void init() throws SchedulerException {
	JobDetail job = JobBuilder.newJob(StatsCollectorJob.class)
	    .withIdentity("collectJob", "statsCollectorGroup")
	    .build();

        job.getJobDataMap().put("statistic", statistic);
        job.getJobDataMap().put("arrayStatistic", arrayStatistic);

	Trigger trigger = TriggerBuilder.newTrigger().
            withIdentity("collectTrigger", "statsCollectorGroup").
            withSchedule(SimpleScheduleBuilder.simpleSchedule().
                         withIntervalInMilliseconds(statisticTimeoutMsec).
                         repeatForever()).
            startNow().
            build();

	// Schedule the job with the trigger
	quartzScheduler.scheduleJob(job, trigger);

        if(quartzScheduler.isStarted() == false)
            quartzScheduler.start();
    }

    public void cleanUp() throws SchedulerException{
        quartzScheduler.unscheduleJob(new TriggerKey("collectTrigger", "statsCollectorGroup"));
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
                statistic.put(from, new AtomicLong(0L));
            
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
    public Map<String, List<List<Long>>> getArrayStatistic(){
        return arrayStatistic;
    }

    /**
     * Setter for quartzScheduler field
     * @param scheduler Scheduler to be set
     */
    public void setScheduler(Scheduler scheduler){
        quartzScheduler = scheduler;
    }

    /**
     * Getter for quartzScheduler field
     * @return value of quartzScheduler
     */
    public Scheduler getScheduler(){
        return quartzScheduler;
    }

    /**
     * Setter for statisticTimeoutMsec field
     * @param timeout value to be set
     */
    public void setStatisticTimeoutMsec(Integer timeout){
        statisticTimeoutMsec = timeout;
    }

    /**
     * Getter for statisticTimeoutMsec field
     * @return value of statisticTimeoutMsec
     */
    public Integer getStatisticTimeoutMsec(){
        return statisticTimeoutMsec;
    }

}


