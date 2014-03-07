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

import grails.converters.JSON
import groovy.json.JsonSlurper

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
	
  @Before
  void setUp() {
    // Setup logic here
    context = new DefaultCamelContext();
    exchange = new DefaultExchange(context);
	
    statsCollector = new StatsCollector(statisticTimeoutMsec: 1000); // seeting delay as 1 second
	
    // autowiring field
    statsCollector.quartzScheduler = new StdSchedulerFactory().getScheduler();
	
    statsCollector.init(); // calling init method
  }

  @After
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

    controller.statsCollector = statsCollector
    params.statsFrom = "abc"
    controller.index()

    def slurper = new JsonSlurper()
    def json = slurper.parseText(response.text)


    Map<Integer, Object> stack = new HashMap<Integer, Object>();
    15.times {
      stack.put(it + 1, null);
    }
    json.each{ arr ->
      stack.remove(arr[1])
    }
    assert stack.size() == 0; // asserting that removed all elements
  }

  void testList() {
    controller.statsCollector = statsCollector
    controller.list() 
    def json = JSON.parse(response.text)
    json.list.each { v ->
      assert statsCollector.arrayStatistic[v] != null
    }
  }

  void testTimeout() {
    controller.statsCollector = statsCollector
    controller.timeout() 
    assert response.text == "{\"timeout\":${statsCollector.statisticTimeoutMsec}}"
  }

}
