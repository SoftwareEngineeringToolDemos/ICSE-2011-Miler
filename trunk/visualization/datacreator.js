/*
 * Function to manage a json object containing a list of mails
 * and construct a javascript list of mail
 * optionally can be passed the function to call to create datas
 */
function dataManagement(jsonObject, fun){
	
	fun = typeof(fun) != 'undefined' ? fun : createDataForMonthBarChart;
	
	var container=document.getElementById("mails")
	var mailList=[]
	//alert(jsonObject.rows)
	for(var k=0; k<jsonObject.length; k++){
		rows=jsonObject[k].rows
	
		for (var i=0; i<rows.length; i++){
			//alert(JSON.stringify(rows[i]))
			var date=new Date(extractDate(rows[i].value.header))
			var subject = extractSubject(rows[i].value.header);
			var sender = extractSender(rows[i].value.header);
			var list=extractList(rows[i].value.header);
			var content=rows[i].value.body
			var newMail = new Mail(subject, sender, date, list, content)
			mailList.push(newMail)
		}
	}
	
	var text = document.createTextNode(mailList)
	container.appendChild(text)

	fun(mailList)
}

/*
 * Function to create the data wich can be used to draw a chart
 * and draw the chart
 */
function createDataForMonthBarChart(mailList) {
	var old = getOldestDate(mailList)
	old=new Date(old.getFullYear(), old.getMonth())
	var recent = getNewestDate(mailList)
	recent=new Date(recent.getFullYear(), recent.getMonth())
	//alert(old.toString() + "  "+ recent.toString())
	
	//initialization of the data for the bar-chart
	var datas=[]
	var d = old
	while(!(d.getFullYear()==recent.getFullYear() && d.getMonth()==recent.getMonth())){
		datas.push({"date":d, "value":0})
		if((d.getMonth()+1)==12)
			d=new Date(d.getFullYear()+1, 0)
		else
			d=new Date(d.getFullYear(), d.getMonth()+1)
		//alert(d.toString() + "  "+ recent.toString())
	}
	datas.push({"date":d, "value":0})
	//alert(datas.length)
		
	//fill the data
	var max=0;
	for(var i=0; i<mailList.length; i++){
		var dt=mailList[i].date
		for(var j=0; j<datas.length; j++){
			if(dt.getFullYear()==datas[j].date.getFullYear() && dt.getMonth()==datas[j].date.getMonth()){
				datas[j].value=datas[j].value+1;
				if(datas[j].value>max)
					max=datas[j].value;
			}
		}
	}
	dataStored=datas
	maxStoredValue=max
	createBarChart(datas, max)	
}
