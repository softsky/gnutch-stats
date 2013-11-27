package gnutch.stats

// Automatic marshalling of XML and JSON
import grails.converters.*


class StatsCollectorController {

    def index() { 
      render (stats:[a:1, b:2]) as JSON
    }
}
