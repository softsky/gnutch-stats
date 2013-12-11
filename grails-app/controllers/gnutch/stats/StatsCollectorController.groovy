package gnutch.stats

// Automatic marshalling of XML and JSON
import grails.converters.*

import org.springframework.beans.factory.annotation.Autowired

class StatsCollectorController {
	
	//@Autowired //autowiring is not working for unit testing but it works fine without it
	StatsCollector statsCollector
	
    def index() { 		 

		Map<String, List<Long>> arrayStatistic = statsCollector.getArrayStatistic()
		 
		def json = []
		 
		int i = 0
		for (entry in arrayStatistic) {
			Map m = new HashMap()
			m.put('name', entry.key)
			m.put('data', entry.value)
			json[i++] = m
		}
		render json as JSON
    }
}
