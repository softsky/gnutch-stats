$(function() {	
    
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
    
    $('#container2').highcharts({
        chart: {
	    events : {
		load : function() {
		    var series0 = this.series[0];
		    var series1 = this.series[1];
		    var series2 = this.series[2];
		    var series3 = this.series[3];
		    setInterval(function() {
			series0.setData([Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]);
			series1.setData([Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]);
			series2.setData([Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]);
			series3.setData([Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]);
		    }, 5000);
		}
	    },
            type: 'column'
        },
        title: {
            text: 'BarChart'
        },
        xAxis: {
            categories: [
                'Jan',
                'Feb',
                'Mar',
                'Apr',
                'May',
                'Jun',
                'Jul',
                'Aug',
                'Sep',
                'Oct',
                'Nov',
                'Dec'
            ]
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Rainfall (mm)'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: 
	[{
            name: 'Tokyo',
            data: [Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]
            
        }, {
            name: 'New York',
            data: [Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]
            
        }, {
            name: 'London',
            data: [Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]
            
        }, {
            name: 'Berlin',
            data: [Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100), Math.round(Math.random() * 100)]
            
        }]
    });
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
                            
		        }, 15000);
		    }
	        }
	    },
	    
	    rangeSelector: {
	        buttons: [{
		    count: 1,
		    type: 'minute',
		    text: '1M'
	        }, {
		    count: 5,
		    type: 'minute',
		    text: '5M'
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
    });
    
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
                            
			}, 15000);
		    }
		}
	    },
	    
	    rangeSelector: {
		buttons: [{
		    count: 1,
		    type: 'minute',
		    text: '1M'
		}, {
		    count: 5,
		    type: 'minute',
		    text: '5M'
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
