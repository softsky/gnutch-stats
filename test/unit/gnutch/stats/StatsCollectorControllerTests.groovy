package gnutch.stats

import grails.test.mixin.*
import grails.test.mixin.support.*

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import org.junit.*
import org.quartz.impl.StdSchedulerFactory

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
		exchange.in.headers['statsFrom'] = 'abc'
    	statsCollector.collect(exchange);		
		statsCollector.collect(exchange);

		Thread.sleep(800); // waiting for 0.8 second (less than the statistic timeout) so job is fired

		controller.statsCollector = statsCollector
		controller.index()
		assert response.text == '[{"name":"abc","data":[2]}]';
    }
}
