/*
 * Creates a line chart with the given data (an array of mapping dates and value)
 * and the max vaule present in the array
 */
function createLineChart(data, maxValue){
	var w = 25,h = (maxValue+1)*18;
	
	var x = d3.scale.linear()
	  .domain([0, 1])
	  .range([0, w]);
  
	var y = d3.scale.linear()
	  .domain([0, 100])
	  .rangeRound([0, h]);
	  
	var rem=d3.select(".chart")
	rem.remove()
	
	var width= w * (data.length+1) + w*5;
	
	//get the html element which will contain the chart and set attributes  
	var chart=d3.select(".chartContainer")
	.append("svg:svg")
     .attr("class", "chart")
     .attr("width", width)
     .attr("height", h+5);
     
    //put the data
   chart.selectAll("circle")
     .data(data)
   .enter().append("svg:circle")
     .attr("cx", function(d, i) { return x(i) + w; })
     .attr("cy", function(d) { return h - h/(maxValue+1)*d.value; })
     .attr("onmouseover", function(d){return "visibleText('"+dateToNiceString(d.date)+"')"})
     .attr("onmouseout", function(d){return "invisibleText('"+dateToNiceString(d.date)+"')"})
     .attr("onclick", function(d){return "lineSelected('"+niceMonth(d.date)+"','"+d.date.getFullYear()+"')"})
     .attr("r", "6");
   //make text for circles
   chart.selectAll("text")
     .data(data)
   .enter().append("svg:text")
     .attr("class", "colored")
     .attr("x", function(d, i) { return x(i) + w/2; })
   	 .attr("y", function(d) { return h - h/(maxValue+1)*d.value -5; })
   	 .attr("visibility", "hidden")
   	 .attr("id",function(d){return dateToNiceString(d.date)})  
   	 .text(function(d){return dateToNiceString(d.date)+" #emails:"+d.value});
     
   //make the bottom line
     chart.append("svg:line")
     .attr("x1", 0)
     .attr("x2", w * (data.length+1))
     .attr("y1", h - .5)
     .attr("y2", h - .5)
     .attr("stroke", "#000");
     
    //make the line references
    var numLines=Math.floor(maxValue/5)
	chart.selectAll("line")
     .data(y.ticks(numLines))
   .enter().append("svg:line")
     .attr("x1", 0)
     .attr("x2", width)
     .attr("y1", y)
     .attr("y2", y)
     .attr("stroke", "#ccc");
     
    //text on the line references
     for(var i=0; i<=numLines; i++){
     	 chart.append("svg:text")
     	 	.attr("class", "rule")
     	 	.attr("x", 0)
     	 	.attr("y", h - h/(maxValue+1)*(i*5))
     	 	.attr("dx", 10)
     	 	.attr("text-anchor", "middle")
     	 	.text(i*5);
     }
    
   //draw the lines
   var circles = chart.selectAll("circle");
   for(var j=0; j<circles[0].length-1; j++){
   	   startX=circles[0][j].getAttribute("cx")
   	   endX=circles[0][j+1].getAttribute("cx")
   	   startY=circles[0][j].getAttribute("cy")
   	   endY=circles[0][j+1].getAttribute("cy")
   	   chart.append("svg:line")
   	   	.attr("x1", startX)
   	   	.attr("x2", endX)
   	   	.attr("y1", startY)
   	   	.attr("y2", endY)
   	   	.attr("stroke", "#70B3F4")
   	   	.attr("stroke-width", 3);
   }
}

function visibleText(id) {
	var text=document.getElementById(id)
	text.setAttribute("visibility","visible");
}
function invisibleText(id){
	var text=document.getElementById(id)
	text.setAttribute("visibility","hidden");
}
