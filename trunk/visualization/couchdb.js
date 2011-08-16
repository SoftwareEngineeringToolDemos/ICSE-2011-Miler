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
 	  this.queryForClass= function(classname, functionToCall) {  	  
 	  	  uri = "http://"+server+":"+port+"/"+database+"/_design/"+searchMethod+"-"+classname+"/_view/"+searchMethod+"?callback=?";
 	  	  //alert(uri) 	  	  
 	  	  this.makeAjaxCall(uri, functionToCall) 	  	  
  	  }
 	  
  	  /*
  	   * Make the http requesto to couchdb
  	   */
 	  this.makeAjaxCall = function(ajaxUrl, functionToCall) {
		 $.ajax({
				type: "GET",
				url: ajaxUrl,
				dataType: "jsonp",
				accepts: "application/json",
				success: function(data){functionToCall(data)},
				error: function(jqXHR, textStatus, errorThrown){alert("failure "+ textStatus +" - "+errorThrown+ " - "+jqXHR.status)}
		  });
	  }
 }
 
 
