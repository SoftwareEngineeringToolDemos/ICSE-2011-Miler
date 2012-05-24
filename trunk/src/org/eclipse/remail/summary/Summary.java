package org.eclipse.remail.summary;

import org.eclipse.remail.Mail;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Summary {
	
	private Mail mail;
	private String summary;
	private String shortSummary;
	private int lengthSummary;
	private MaxentTagger tagger;
	private FeaturesExtractor extractor; 
	
	public Summary(Mail mailToSumm, MaxentTagger taggerMail){
		mail = mailToSumm;
		tagger = taggerMail;
		extractor = new FeaturesExtractor(mail, tagger);
		lengthSummary = 0;
		summary = summUpMail();
		shortSummary = makeShortSummary();
	}
	
	
	private String summUpMail(){
		String summary = "";	
		for(int i=0; i<extractor.getMailLength(); i++){
			if(extractor.getRelevanceAtPosition(i) == 1.0)
				lengthSummary++;
				summary += extractor.getSentenceAtPosition(i) + "\n\n";
		}
		return summary;
	}
	
	
	private String makeShortSummary(){
		if(extractor.getMailLength() < 4){
			return summary;
		} else {
			String shortSummary = "";
			int lengthShortSummary = 0;
			int correctLength = (int) Math.ceil(lengthSummary*60/100);
			double depth = 0.0;
			int i = 0;
			while(i < extractor.getMailLength() && lengthShortSummary < correctLength){
				if(extractor.getRelevanceAtPosition(i) == 1.0 && extractor.getDepthAtPosition(i) == depth){
					lengthShortSummary++;
					shortSummary += extractor.getSentenceAtPosition(i) + "\n\n";
				}
				if(i == extractor.getMailLength()-1 && depth <= 6.0){
					i = 0;
					depth = depth + 1.0;
				} else {
					i++;
				}
			}
			return shortSummary;
		}
	}
	
	
	public String getSummary(){
		return summary;
	}
	
	
	public String getShortSummary(){
		return shortSummary;
	}
	
	
	

}
