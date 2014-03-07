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

  @Before
  public void setUp(){
    super.setUp()
  }

  @After
  public void tearDown(){
    super.tearDown()
  }

  @Test
  void testStatistic() {
    final unchanged = [1,2,3]
    println camelContext.routeDefinitions

    assert statsCollector.statistic.size() == 0

    def mockEndpoint = getMockEndpoint('mock:direct:c')

    def expectation = {
      def ex = receivedExchanges[0]
      assert ex.in.body.equals(unchanged)
    }
    expectation.delegate = mockEndpoint

    mockEndpoint.expects(expectation)
    mockEndpoint.expectedMessageCount(3)

    template.sendBody('direct:a', unchanged)

    assertMockEndpointsSatisfied()

    assert statsCollector.statistic['direct:a'].get() == 3
    assert statsCollector.statistic['direct:b'].get() == 3
    assert statsCollector.statistic['direct:c'].get() == 3

  }
}
