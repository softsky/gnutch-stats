import gnutch.stats.StatsCollector
import grails.util.Environment

import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader

import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler

class GnutchStatsGrailsPlugin {
    def version = "0.1.17"
    def grailsVersion = "2.2 > *"
    def pluginExcludes = [
    ]

    def loadAfter = ['controllers', 'services', 'routing', 'gnutch']
    def title = "Grails Gnutch Statistics plugin"
    def documentation = "http://grails.org/plugin/gnutch"

    def license = "APACHE"
    def developers = [
      [name: "Arsen A. Gutsal", email: "gutsal.arsen@gmail.com"]
    ]

    def issueManagement = [ system: "GitHub", url: "https://github.com/softsky/gnutch-stats/issues" ]
    def scm = [ url: "https://github.com/softsky/gnutch-stats" ]
    def description = 'Statistics for gnutch Grails plugin'

    def doWithSpring = {

      stdSchedulerFactory(StdSchedulerFactory)

      quartzScheduler(Scheduler){ bean ->
	bean.factoryBean = "stdSchedulerFactory"
	bean.factoryMethod = "getScheduler"
        bean.initMethod = "start"
       }
 
      statsCollector(StatsCollector){ bean ->
	scheduler = ref("quartzScheduler")
        statisticTimeoutMsec = 30000L
	bean.initMethod = "init"	
        bean.destroyMethod = "cleanUp"
      }
      
    }

    def doWithApplicationContext = { applicationContext ->
      def config = application.config.gnutch.stats

      if(Environment.current == Environment.TEST){
        def xmlBeans = applicationContext.getBeanFactory()
        new XmlBeanDefinitionReader(xmlBeans).loadBeanDefinitions('test/integration/resources/applicationContext.xml')

        // TODO remove this snippets
        def camelContext = applicationContext.getBean(config?.camelContextId)
        println "Routes: " + camelContext.routeDefinitions
      }

      if(applicationContext.containsBean(config?.camelContextId)){
        final CamelContext camelContext = applicationContext.getBean(config.camelContextId)

        def routeDefinitions = []
        camelContext.routeDefinitions.each { routeDefinition ->
          routeDefinitions << routeDefinition
        }

        routeDefinitions.each { routeDefinition ->
          routeDefinition.inputs.each { input ->
            def clos = [configure: {
              interceptFrom(input.uri).
              setHeader('statsFrom', constant("${input.uri}")).
              beanRef('statsCollector', 'collect')
            }]
            clos.configure.delegate = clos as RouteBuilder
            routeDefinition.adviceWith(camelContext, clos.configure.delegate)
          }
        }
      }

      if(Environment.current == Environment.TEST){
        applicationContext.getBean(config?.camelContextId).start()
      }

    }
}
