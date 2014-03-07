package gnutch.stats

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.CountDownLatch


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
    CountDownLatch signal = new CountDownLatch(1);

    exchange.in.headers['statsFrom'] = 'abc'
    statsCollector.collect(exchange)
    assert statsCollector.statistic['abc'].get() == 1L
    statsCollector.collect(exchange)
    assert statsCollector.statistic['abc'].get() == 2L

    def clos = { -> 
      exchange.in.headers['statsFrom'] = 'loop'
      1000.times { statsCollector.collect(exchange); }

      Thread.sleep(5000); // waiting for 5 seconds to complete

      signal.countDown()
    }

    def thread = Thread.start(clos);
	
    signal.await()

    assert statsCollector.arrayStatistic.containsKey('loop')
    def arr = statsCollector.arrayStatistic['loop'].get(0)
    def val = statsCollector.statistic['loop'].get()

    assert arr[1] + val == 1000L:'The sum should be exactly 1000'

  }

  void testThreadSafetyDifferentHeaders() {
    def nThreads = 10;

    ExecutorService pool = Executors.newFixedThreadPool(nThreads)
    nThreads.times {
      Runnable r = { -> 
        Exchange exchange = new DefaultExchange(context);
        exchange.in.headers['statsFrom'] = 'loop' + it;
        1000.times { statsCollector.collect(exchange); }
      }

      pool.execute(r);
    }
    
    Thread.sleep(2000); // waiting for 5 second so job is fired

    pool.shutdown();
    pool.awaitTermination(10, TimeUnit.SECONDS); // let it work 2 seconds

    nThreads.times {
      assert statsCollector.arrayStatistic.containsKey('loop' + it)
      def arr = statsCollector.arrayStatistic['loop' + it].get(0)
      def val = statsCollector.statistic['loop' + it].get()

      assert arr[1] + val == 1000L: 'The sum should be exactly 1000L'
    }
 
  }

  void testThreadSafetySameHeader() {
    def nThreads = 10;

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

    assert statsCollector.arrayStatistic.containsKey('loop')
    def arr =  statsCollector.arrayStatistic['loop'].get(0)
    def val = statsCollector.statistic['loop'].get()
  
    assert arr[1] + val == 10 * 1000L: 'The sum should be exactly 10*1000L'
 
  }


}
