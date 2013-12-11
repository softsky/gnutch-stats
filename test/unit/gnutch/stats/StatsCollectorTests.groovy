package gnutch.stats

import static org.junit.Assert.*
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange

@TestMixin(GrailsUnitTestMixin)
class StatsCollectorTests {

  def context
  def exchange

  def statsCollector

  void setUp() {
    // Setup logic here
    context = new DefaultCamelContext()
    exchange = new DefaultExchange(context)

    statsCollector = new StatsCollector(statisticTimeoutMsec: 1000) // seeting delay as 1 second
  }

  void testCollect() {
    exchange.in.headers['statsFrom'] = 'abc'
    statsCollector.collect(exchange)
    assert statsCollector.statistic['abc'].get() == 1L
    statsCollector.collect(exchange)
    assert statsCollector.statistic['abc'].get() == 2L

    def thread = Thread.start({->
                   exchange.in.headers['loop'] = 'abc'
                   1000.times { statsCollector.collect(exchange) }
                   Thread.sleep(1000)
                              })

    thread.join(2000) // let it work 2 seconds

    assert statsCollector.arrayStatistic.containsKey('loop') &&
    (statsCollector.arrayStatistic['loop'].get(0) + statsCollector.statistic['loop'].get()) == 1000L

  }
}
