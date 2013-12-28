package gnutch.stats;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StatsCollectorJob implements Job {
    private static Logger LOG = LoggerFactory.getLogger(StatsCollectorJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.trace("StatsCollectorJob executed");
        JobDataMap map = context.getJobDetail().getJobDataMap();
        
        Map<String, AtomicLong> statistic = (Map<String, AtomicLong>)map.get("statistic");
        Map<String, List<List<Long>>> arrayStatistic = (Map<String, List<List<Long>>>)map.get("arrayStatistic");
        synchronized(statistic){
            synchronized(arrayStatistic){
                Iterator<Map.Entry<String, AtomicLong>> i = statistic.entrySet().iterator();
                while(i.hasNext()){
                    Map.Entry<String, AtomicLong> e = i.next();
                    if(arrayStatistic.containsKey(e.getKey()) == false)
                        arrayStatistic.put(e.getKey(), new ArrayList<List<Long>>());
                    // making new array
                    List<Long> arr = new ArrayList<Long>();

                    arr.add(new Date().getTime());
                    arr.add(e.getValue().get());
                    
                    arrayStatistic.get(e.getKey()).add(arr);
                    e.getValue().set(0L);
                };
            }
        }
    }
}
