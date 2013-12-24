package gnutch.stats

// Automatic marshalling of XML and JSON
import grails.converters.*

import org.springframework.beans.factory.annotation.Autowired

class StatsCollectorController {
	
  StatsCollector statsCollector
	
  def index() { 		 		
    def json = []
		
    if(params.statsFrom) {

      Map<String, List<Long>> arrayStatistic = statsCollector.getArrayStatistic()

      int i = 0
      for (entry in arrayStatistic) {

        if(entry.key == params.statsFrom) {
          Map m = new HashMap()
          m.put('name', entry.key)
          m.put('data', entry.value)
          json[i++] = m
        }
      }
    }

    if(params.callback)
      render "${params.callback}(${json as JSON})"
      else
      render json as JSON
  }

  def list(){
    def json = [:]
    def arrayStatistic = statsCollector.getArrayStatistic()

    json["list"] = []
    
    arrayStatistic.each { k, v ->
      json["list"] << k
    }
    render json as JSON
  }
}
