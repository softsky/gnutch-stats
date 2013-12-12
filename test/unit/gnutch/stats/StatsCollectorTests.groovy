package gnutch.stats

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

import org.quartz.impl.StdSchedulerFactory;

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class StatsCollectorTests {

  def context
  def exchange

  def statsCollector

  void setUp() {
    // Setup logic here
    context = new DefaultCamelContext();
    exchange = new DefaultExchange(context);

    statsCollector = new StatsCollector(statisticTimeoutMsec: 1000); // seeting delay as 1 second

    // autowiring field
    statsCollector.quartzScheduler = new StdSchedulerFactory().getScheduler();

    statsCollector.init(); // calling init method
  }

  void tearDown() {
    // Tear down logic here
    statsCollector.quartzScheduler.shutdown();
  }

  void testCollect() {
    exchange.in.headers['statsFrom'] = 'abc'
    statsCollector.collect(exchange)
    assert statsCollector.statistic['abc'].get() == 1L
    statsCollector.collect(exchange)
    assert statsCollector.statistic['abc'].get() == 2L

    def thread = Thread.start({-> 
                   exchange.in.headers['statsFrom'] = 'loop'
                   1000.times { statsCollector.collect(exchange); }
                   Thread.sleep(1000);
                              });
	
	Thread.sleep(1000); // waiting for 1 second so job is fired

    assert statsCollector.arrayStatistic.containsKey('loop') &&
    (statsCollector.arrayStatistic['loop'].get(0) + statsCollector.statistic['loop'].get()) == 1000L

  }

  void testThreadSafetyDifferentHeaders() {
    def nThreads = 50;

    ExecutorService pool = Executors.newFixedThreadPool(nThreads)
    nThreads.times {
      Runnable r = { -> 
        Exchange exchange = new DefaultExchange(context);
        exchange.in.headers['statsFrom'] = 'loop' + it;
        1000.times { statsCollector.collect(exchange); }
      }

      pool.execute(r);
    }
    
    Thread.sleep(1000); // waiting for 1 second so job is fired


    pool.shutdown();
    pool.awaitTermination(10, TimeUnit.SECONDS); // let it work 2 seconds

    nThreads.times {
      assert statsCollector.arrayStatistic.containsKey('loop' + it) && 
      (statsCollector.arrayStatistic['loop' + it].get(0) + statsCollector.statistic['loop' + it].get()) == 1000L;
    }
 
  }

  void testThreadSafetySameHeader() {
    def nThreads = 50;

    ExecutorService pool = Executors.newFixedThreadPool(nThreads)
    exchange.in.headers['statsFrom'] = 'loop';
    nThreads.times {
      Runnable r = { -> 
        1000.times { statsCollector.collect(exchange); }
      }

      pool.execute(r);
    }
    
    Thread.sleep(1000); // waiting for 1 second so job is fired


    pool.shutdown();
    pool.awaitTermination(10, TimeUnit.SECONDS); // let it work 2 seconds

    assert statsCollector.arrayStatistic.containsKey('loop') && 
    (statsCollector.arrayStatistic['loop'].get(0) + statsCollector.statistic['loop'].get()) == 50 * 1000L;
 
  }


}
