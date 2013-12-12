package gnutch.stats

import static org.junit.Assert.*
import org.junit.*

import org.apache.camel.CamelContext
import org.apache.camel.test.junit4.CamelTestSupport

class InitializationTests extends CamelTestSupport {

  def camelContext
  def statsCollector

  @Override
  protected CamelContext createCamelContext() throws Exception {
    return camelContext
  }

  @Test
  void testStatistic() {
    final unchanged = [1,2,3]

    assert statsCollector.statistic.size() == 0

    def mockEndpoint = getMockEndpoint('mock:direct:a')

    def expectation = {
      def ex = receivedExchanges[0]
      assert ex.in.body.equals(unchanged)
    }
    expectation.delegate = mockEndpoint

    mockEndpoint.expects(expectation)
    mockEndpoint.expectedMessageCount(3)

    template.sendBody('direct:a', unchanged)
    template.sendBody('direct:a', unchanged)
    template.sendBody('direct:a', unchanged)

    assertMockEndpointsSatisfied()

    assert statsCollector.statistic['direct:a'].get() == 3
  }
}
