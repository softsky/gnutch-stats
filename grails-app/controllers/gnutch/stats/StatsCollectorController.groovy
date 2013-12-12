package gnutch.stats

// Automatic marshalling of XML and JSON
import grails.converters.*

import org.springframework.beans.factory.annotation.Autowired

class StatsCollectorController {
	
	//@Autowired //autowiring is not working for unit testing but it works fine without it
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
		render json as JSON
    }
}
