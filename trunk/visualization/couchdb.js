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
 	  	  
 	  	  var mails = new MailCollection(database.length, functionToCall)
 	  	  for (var i=0; i<database.length; i++){
 	  	  	  
			  uri = "http://"+server+":"+port+"/"+database[i]+"/_design/"+searchMethod+"-"+classname+"/_view/"+searchMethod+"?callback=?";
			  //alert(uri) 	  	
			  
			  this.makeAjaxCall(uri, mails) 
 	  	  }	  	  
  	  }
 	  
  	  /*
  	   * Make the http requesto to couchdb and send back the result to functionToCall
  	   */
 	  this.makeAjaxCall = function(ajaxUrl, mails) {
		 $.ajax({
				type: "GET",
				url: ajaxUrl,
				dataType: "jsonp",
				accepts: "application/json",
				success: function(data){mails.receiveSomeData(data)},
				error: function(jqXHR, textStatus, errorThrown){alert("failure "+ textStatus +" - "+errorThrown+ " - "+jqXHR.status)}
		  });
	  }
 }
 
/*
 * It's a class that contains the mail found by querys on couchdb
 */
 function MailCollection(numberOfMailList, functionToCall){
 	 this.numberOfMailList=numberOfMailList
 	 this.functionToCall=functionToCall
 	 
 	 this.mailListLeft=numberOfMailList
 	 
 	 this.dataReceived=[]
 	 
 	 this.receiveSomeData= function(data){
 	 	 this.dataReceived.push(data)
 	 	 this.mailListLeft=this.mailListLeft-1
 	 	 //alert(this.numberOfMailList+" "+this.mailListLeft)
 	 	 if(this.mailListLeft==0)
 	 	 	 this.functionToCall(this.dataReceived)
 	 }
 }
