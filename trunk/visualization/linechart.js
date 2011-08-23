/*
 * Creates a line chart with the given data (an array of mapping dates and value)
 * and the max maule present in the array
 */
function createLineChart(data, maxValue){
	var w = 25,h = maxValue*18;
	
	var x = d3.scale.linear()
	  .domain([0, 1])
	  .range([0, w]);
  
	var y = d3.scale.linear()
	  .domain([0, 100])
	  .rangeRound([0, h]);
	  
	var chart=d3.select(".chartContainer")
	chart.remove()
	
	chart.append("svg:svg")
     .attr("class", "chart")
     .attr("width", w * (data.length+1))
     .attr("height", h+200);
     
   var g = vis.append("svg:g")
    .attr("transform", "translate(0, 200)");
     
   var line = d3.svg.line()
    .x(function(d,i) { return x(i); })
    .y(function(d) { return -1 * y(d.value); })
    
    g.append("svg:path").attr("d", line(data));
    
    //axis
    g.append("svg:line")
    	.attr("x1", x(0))
    	.attr("y1", -1 * y(0))
    	.attr("x2", x(w))
    	.attr("y2", -1 * y(0))
 
    g.append("svg:line")
    	.attr("x1", x(0))
    	.attr("y1", -1 * y(0))
    	.attr("x2", x(0))
    	.attr("y2", -1 * y(d3.max(data)))
    	
    g.selectAll(".xLabel")
    .data(x.ticks(5))
    .enter().append("svg:text")
    .attr("class", "xLabel")
    .text(String)
    .attr("x", function(d) { return x(d) })
    .attr("y", 0)
    .attr("text-anchor", "middle")
 
    g.selectAll(".yLabel")
		.data(y.ticks(4))
		.enter().append("svg:text")
		.attr("class", "yLabel")
		.text(String)
		.attr("x", 0)
		.attr("y", function(d) { return -1 * y(d) })
		.attr("text-anchor", "right")
		.attr("dy", 4)
		
	g.selectAll(".xTicks")
		.data(x.ticks(5))
		.enter().append("svg:line")
		.attr("class", "xTicks")
		.attr("x1", function(d) { return x(d); })
		.attr("y1", -1 * y(0))
		.attr("x2", function(d) { return x(d); })
		.attr("y2", -1 * y(-0.3))
	 
    g.selectAll(".yTicks")
		.data(y.ticks(4))
		.enter().append("svg:line")
		.attr("class", "yTicks")
		.attr("y1", function(d) { return -1 * y(d); })
		.attr("x1", x(-0.3))
		.attr("y2", function(d) { return -1 * y(d); })
		.attr("x2", x(0))
}
