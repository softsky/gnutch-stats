package gnutch.stats;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;


import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StatsCollectorJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        
        Map<String, AtomicLong> statistic = (Map<String, AtomicLong>)map.get("statistic");
        Map<String, List<Long>> arrayStatistic = (Map<String, List<Long>>)map.get("arrayStatistic");
        synchronized(statistic){
            synchronized(arrayStatistic){
                Iterator<Map.Entry<String, AtomicLong>> i = statistic.entrySet().iterator();
                while(i.hasNext()){
                    Map.Entry<String, AtomicLong> e = i.next();
                    if(arrayStatistic.containsKey(e.getKey()) == false)
                        arrayStatistic.put(e.getKey(), new ArrayList<Long>());
                    arrayStatistic.get(e.getKey()).add(e.getValue().get());
                    e.getValue().set(0L);
                };
            }
        }
    }
}
