/*
 * Defines a class to store mails
 */
function Mail(subject, sender, date, list, body) {
	this.subject=subject
	this.sender=sender
	this.date=date
	this.list=list
	this.body=body
	
	/*
	 * Return the year relative to the date when the mail has been sent
	 */
	this.getYear = function() {
		return date.getFullYear()
	}
	
	/*
	 * Return the month relative to the date when the mail has been sent
	 */
	this.getMonth = function() {
		return date.getMonth()+1;
	}
}


/*
 * Extract the date header
 */
function extractDate(headers) {		
	//split the string
	hds=headers.split("', '")	
	for (var i=0; i<hds.length; i++){	
		var n=hds[i].indexOf("Date:")
		if(n!=-1)
			return hds[i].substring(n+"Date:".length,hds[i].length);
	}
}

/*
 * Extract the subject header
 */
function extractSubject(headers) {
	//split the string
	hds=headers.split("', '")	
	for (var i=0; i<hds.length; i++){		
		if(hds[i].indexOf("Subject:")!=-1)
			return hds[i];
	}
}
 
/*
 * Extract the sender header (From)
 */
function extractSender(headers) {
	//split the string
	hds=headers.split("', '")	
	for (var i=0; i<hds.length; i++){		
		if(hds[i].indexOf("From:")!=-1)
			return hds[i];
	}
}

/*
 * Extract the list header
 */
function extractList(headers) {
	//split the string
	hds=headers.split("', '")	
	for (var i=0; i<hds.length; i++){		
		if(hds[i].indexOf("List:")!=-1)
			return hds[i];
	}
}


/*
 * Get the oldest mail in the mail Array and returns it's date
 */
function getOldestDate(mailsArray) {
	old=mailsArray[0].date
	for(var i=1; i<mailsArray.length; i++){
		if(mailsArray[i].date<old)
			old=mailsArray[i].date
	}
	return old
}

/*
 * Get the most recent mail in the mailing list and return it's date
 */
function getNewestDate(mailsArray) {
	recent=mailsArray[0].date
	for(var i=1; i<mailsArray.length; i++){
		if(mailsArray[i].date>recent)
			recent=mailsArray[i].date
	}
	return recent
}

/*
 * Convert the given date to a nice string "Month Year"
 */
function dateToNiceString(date){
	var month=new Array(12);
	month[0]="January";
	month[1]="February";
	month[2]="March";
	month[3]="April";
	month[4]="May";
	month[5]="June";
	month[6]="July";
	month[7]="August";
	month[8]="September";
	month[9]="October";
	month[10]="November";
	month[11]="December";
	
	return month[date.getMonth()]+" "+date.getFullYear()
}