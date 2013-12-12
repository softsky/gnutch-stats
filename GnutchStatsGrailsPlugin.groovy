import gnutch.stats.StatsCollector
import grails.util.Environment

import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader

class GnutchStatsGrailsPlugin {
    def version = "0.1.4"
    def grailsVersion = "2.0 > *"
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
      statsCollector(StatsCollector)
    }

    def doWithApplicationContext = { applicationContext ->
      def config = application.config.gnutch.stats

      if(Environment.current == Environment.TEST){
        def xmlBeans = new DefaultListableBeanFactory()
        new XmlBeanDefinitionReader(xmlBeans).loadBeanDefinitions('classpath:resources/applicationContext.xml')
        xmlBeans.beanDefinitionNames.each { name ->
          println "Registering bean: ${name}"
          applicationContext.registerBeanDefinition(name, xmlBeans.getBeanDefinition(name))
        }
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
              beanRef('statsCollectorService', 'collect')
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
