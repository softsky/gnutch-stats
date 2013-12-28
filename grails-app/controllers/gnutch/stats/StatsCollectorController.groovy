package gnutch.stats

// Automatic marshalling of XML and JSON
import grails.converters.*

import org.springframework.beans.factory.annotation.Autowired

class StatsCollectorController {
	
  StatsCollector statsCollector
	
  def index() { 		 		
    def json = []
		
    if(params.statsFrom) {
      def arrayStatistic = statsCollector.getArrayStatistic()
      json = arrayStatistic[params.statsFrom]
    }

    if(params.callback)
      render(contentType: 'application/json', text: "${params.callback}(${json as JSON})")
      else
      render(contentType: 'application/json', text: json as JSON)
  }

  def list(){
    def json = [:]
    def arrayStatistic = statsCollector.getArrayStatistic()

    json["list"] = []
    
    arrayStatistic.each { k, v ->
      json["list"] << k
    }

    render(contentType: 'application/json', text: json as JSON)
  }

  def timeout(){
    def json = [timeout: statsCollector.statisticTimeoutMsec]
    render(contentType: 'application/json', text: json as JSON)
  }

}
