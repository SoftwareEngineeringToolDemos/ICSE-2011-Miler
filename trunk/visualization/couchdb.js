/*
 * Defines a class to handle the connection to couchdb
 */
 function CouchDBConnect(server, port, database, searchMethod){
 	 this.server=server //server name
 	 this.port=port //server port
  	 this.database=database //the database name
 	 this.searchMethod=searchMethod //the name of the search method to use (ie casesensitive)
 	  
 	 /*
 	  * Defines a function to get the result of a query
 	  */
 	  this.queryForClass= function(classname) {  	  
 	  	  uri = server+":"+port+"/"+database+"/_design/"+searchMethod+"-"+classname+"/_view/"+searchMethod
 	  	  alert(uri)
 	  	  // var req = new XMLHttpRequest()
 	  	  // req.onreadystatechange = function() {
 	  	  	  // if (req.readyState != 4) 
 	  	  	  	  // return req.responseText;
 	  	  // }
 	  	  // req.open("GET", uri, true)
 	  	  // req.send(null);
 	  	  // var response = $.getJSON(uri, function(data) {
 	  	  		  // var items = [];
// 
 	  	  		  // $.each(data, function(key, val) {
 	  	  		  		  // items.push(key, val);
 	  	  		  // });
 	  	  		  // alert(items)
 	  	  // });
 	  	  //alert(JSON.stringify(response))
 	  	  jQuery.makeAjaxCall(uri, alert, alert)
 	  }
 }
 jQuery.makeAjaxCall = function(ajaxUrl, functionSuccess, functionFailure) {
 $.ajax({
    type: "GET",
        url: ajaxUrl,
        contentType: "application/json; charset=utf-8",
        data: {},
        dataType: "json",
        success: function(){alert(data)},
        error: function(){alert("failure")}
  });
 }
 
