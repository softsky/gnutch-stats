import grails.util.Environment
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.CamelContext

import gnutch.stats.StatsCollectorService

class GnutchStatsGrailsPlugin {
    // the plugin version
    def version = "0.1.3"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
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
    def description = '''\
Statistics for gnutch grails plugin
'''
    def doWithSpring = {
      statsCollector(StatsCollectorService)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
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
        final CamelContext camelContext = applicationContext.getBean(config.camelContextId);

      
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
            def rb = clos as RouteBuilder
            clos.configure.delegate = rb
            routeDefinition.adviceWith(camelContext, rb)
          }
        }
      }

      if(Environment.current == Environment.TEST){
        applicationContext.getBean(config?.camelContextId).start()
      }

    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}