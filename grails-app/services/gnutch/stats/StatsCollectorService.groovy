package gnutch.stats

import org.apache.camel.Exchange
import java.util.concurrent.atomic.AtomicLong

class StatsCollectorService {
  static transactional = false

  Map<String, AtomicLong> statistic = new HashMap<String, AtomicLong>()

  void collect(Exchange ex) {
    def from = ex.in.headers['statsFrom'].toString()
 
    synchronized(statistic){
      if(statistic.containsKey(from) == false)
        statistic[from] = new AtomicLong()

        statistic[from].incrementAndGet();
    }
  }
}
