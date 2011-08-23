/*
 * Creates a bar chart with the given data (an array of mapping dates and value)
 * and the max maule present in the array
 */
function createBarChart(data, maxValue){	
	
	//alert(data[data.length-1].date.toString())
	//size of the chart
	var w = 25,h = maxValue*18;
  
	var x = d3.scale.linear()
	  .domain([0, 1])
	  .range([0, w]);
  
	var y = d3.scale.linear()
	  .domain([0, 100])
	  .rangeRound([0, h]);
	
	var rem=d3.select(".chart")
	rem.remove()
	  
	//get the html element which will contain the chart and set attributes     	
	var chart=d3.select(".chartContainer")
  .append("svg:svg")
     .attr("class", "chart")
     .attr("width", w * (data.length+1))
     .attr("height", h+200);
  
   //put the data
   chart.selectAll("rect")
     .data(data)
   .enter().append("svg:rect")
     .attr("x", function(d, i) { return x(i) + w/2; })
     .attr("y", function(d) { return h - h/maxValue*d.value; })
     .attr("width", w)
     .attr("height", function(d) { return h/maxValue*d.value; })
     .attr("onclick", function(d){return "barSelected('"+niceMonth(d.date)+"','"+d.date.getFullYear()+"')"});
     
     //make the bottom line
     chart.append("svg:line")
     .attr("x1", 0)
     .attr("x2", w * (data.length+1))
     .attr("y1", h - .5)
     .attr("y2", h - .5)
     .attr("stroke", "#000");
     
     //add a text to the bar
     chart.selectAll("text")
     .data(data)
   .enter().append("svg:text")
     .attr("x", function(d, i) { return x(i) + w/2 +2})
     .attr("y", function(d) { return h - h/maxValue*(d.value-1) - h/maxValue*0.1})
     .text(function(d) { return d.value});
   
     
     //add a text to the bottom line  
     for(var i=0; i<data.length; i++){
     	 chart.append("svg:text")
     	 	.attr("class", "line")
     	 	.attr("x", w*i + w/2 +4)
     	 	.attr("y", h+5)
     	 	.attr("transform", rotateText(w*i + w/2 +4, h+5))
     	 	.text(dateToNiceString(data[i].date));
     } 
     
}

/*
 * It rotatates the text
 */
function rotateText(x,y) { 
	return "rotate(90 "+x+" "+y+")"
}