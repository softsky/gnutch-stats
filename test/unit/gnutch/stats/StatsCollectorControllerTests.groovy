package gnutch.stats

import grails.test.mixin.*
import grails.test.mixin.support.*

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.junit.*
import org.quartz.impl.StdSchedulerFactory

import java.util.concurrent.CountDownLatch

import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;

import org.quartz.Matcher;

import org.quartz.JobExecutionContext;


/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class StatsCollectorControllerTests {

  def context;
  def exchange;

  def statsCollector;
	
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

  void testIndex() {	
    CountDownLatch signal = new CountDownLatch(15);

    def triggerListener = new TriggerListener(){

      @Override
      public String getName(){
        return 'collectTrigger'
      };

      @Override
      public void triggerFired(Trigger trigger, JobExecutionContext context){
        signal.count.times {
          exchange.in.headers['statsFrom'] = 'abc'
          statsCollector.collect(exchange);
        }
        signal.countDown();
      }

      @Override
      public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context){
      }

      @Override
      public void triggerMisfired(Trigger trigger){
      }

      @Override
      public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                  Trigger.CompletedExecutionInstruction completeexecutioninstruction){
      }
    }
    
    // this trigger listener will be called (in sync with default job from StatsCollector class)
    statsCollector.
    quartzScheduler.
    listenerManager.
    addTriggerListener(triggerListener)

    signal.await();
    //Thread.sleep(15000)

    controller.statsCollector = statsCollector
    params.statsFrom = "abc"
    controller.index()
    assert response.text == '[{"name":"abc","data":[15,14,13,12,11,10,9,8,7,6,5,4,3,2,1]}]';
  }
}
