$(function() {	
    var timeout;
    $.getJSON("http://localhost:8080/sourcingtool/statsCollector/timeout", function(data){
        timeout = data.timeout;
    })

    
    $('#container1').highcharts({
        chart: {events : {
	    load : function() {
		series=this.series[0];
            }},
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
               },
        title: {
            text: 'PieChart'
        },
        tooltip: {
    	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                }
            }
        },
        series: [{
            type: 'pie',
            name: 'Browser share',
            data: [
                ['Firefox',   40.0],
                ['IE',       26.8],
                ['Chrome', 12.8],
                ['Safari',    8.5],
                ['Opera',     6.2],
                ['Others',   0.7]
            ]
        }]
    });

    $.getJSON("http://localhost:8080/sourcingtool/statsCollector?statsFrom=direct:process-tidy&callback=?", function(data){

	// Create the chart
	$('#container2').highcharts('StockChart', {
	    chart : {
		events : {
		    load : function() {
			// set up the updating of the chart each second
			var series = this.series[0];
			setInterval(function() {
                            $.getJSON("http://localhost:8080/sourcingtool/statsCollector?statsFrom=direct:process-tidy&callback=?", function(data){
                                series.setData(data);
                            })
                            
			}, timeout);
		    }
		}
	    },
	    
	    rangeSelector: {
		buttons: [{
		    count: 15,
		    type: 'minute',
		    text: '15M'
		}, {
		    count: 1,
		    type: 'hour',
		    text: '1H'
		}, {
		    type: 'all',
		    text: 'All'
		}],
		inputEnabled: false,
		selected: 0
	    },
	    
	    title : {
		text : 'direct:process-tidy'
	    },
	    
	    exporting: {
		enabled: false
	    },
	    
	    series : [{
		name : 'data',
		data : data
	    }]
	});
    })

    
    $.getJSON("http://localhost:8080/sourcingtool/statsCollector?statsFrom=direct:process-tika&callback=?", function(data){

	// Create the chart
	$('#container3').highcharts('StockChart', {
	    chart : {
		events : {
		    load : function() {
			// set up the updating of the chart each second
			var series = this.series[0];
			setInterval(function() {
                            $.getJSON("http://localhost:8080/sourcingtool/statsCollector?statsFrom=direct:process-tika&callback=?", function(data){
                                series.setData(data);
                            })
                            
			}, timeout);
		    }
		}
	    },
	    
	    rangeSelector: {
		buttons: [{
		    count: 15,
		    type: 'minute',
		    text: '15M'
		}, {
		    count: 1,
		    type: 'hour',
		    text: '1H'
		}, {
		    type: 'all',
		    text: 'All'
		}],
		inputEnabled: false,
		selected: 0
	    },
	    
	    title : {
		text : 'direct:process-tika'
	    },
	    
	    exporting: {
		enabled: false
	    },
	    
	    series : [{
		name : 'data',
		data : data
	    }]
	});
    })

    Highcharts.setOptions({
	global : {
	    useUTC : false
	}
    });
    
    $.getJSON("http://localhost:8080/sourcingtool/statsCollector?statsFrom=direct:aggregate-documents&callback=?", function(data){

	// Create the chart
	$('#container4').highcharts('StockChart', {
	    chart : {
		events : {
		    load : function() {
			// set up the updating of the chart each second
			var series = this.series[0];
			setInterval(function() {
                            $.getJSON("http://localhost:8080/sourcingtool/statsCollector?statsFrom=direct:aggregate-documents&callback=?", function(data){
                                series.setData(data);
                            })
                            
			}, timeout);
		    }
		}
	    },
	    
	    rangeSelector: {
		buttons: [{
		    count: 15,
		    type: 'minute',
		    text: '15M'
		}, {
		    count: 1,
		    type: 'hour',
		    text: '1H'
		}, {
		    type: 'all',
		    text: 'All'
		}],
		inputEnabled: false,
		selected: 0
	    },
	    
	    title : {
		text : 'direct:aggregate-documents'
	    },
	    
	    exporting: {
		enabled: false
	    },
	    
	    series : [{
		name : 'data',
		data : data
	    }]
	});
    })

});
