package org.eclipse.remail.summary;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.remail.Mail;

public class FeaturesExtractor {

	//	private static final PATTERN = "(?x) \w+\:/\S+ | i\.e\. | e\.g\. | \w+(/\w+)+ | \w+(\\w+)+ | \w+((-|_|\.)(?!(as|java|php|c|h)\W|\w+\()\w+)+ | \w+ | -\d+";

	private String subject;

	// Key: id sentence (from 0). Value: sentence.
	private HashMap<Integer, String> sentencesTable;

	// Col 0: chars, Col 1: num_stopw_norm, Col 2: num_verbs_norm, Col 3: rel_pos_norm, Col 4: subj_words_norm , Col 5: relevant
	private double[][] featuresTable;

	private HashSet<String> stopwords;




	public FeaturesExtractor(Mail mail){
		createStopwords();
		subject = mail.getSubject();
		createSentencesTable(mail.getText());
		createFeaturesTable();
	}




	private void createSentencesTable(String fullContent){
		String[] contentLines = fullContent.split("\n");
		String newContent = "";
		for(int i=0; i<contentLines.length; i++){
			if(contentLines[i] != null && contentLines[i].length() > 0){
				if(contentLines[i].charAt(0) != '>'){
					newContent += contentLines[i];
				}
			}
		}
		BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);
		bi.setText(newContent);
		int index = 0;
		int start = bi.first();
		sentencesTable = new HashMap<Integer, String>();
		for (int end = bi.next(); end != BreakIterator.DONE; start = end, end = bi.next()) {
			sentencesTable.put(index, newContent.substring(start,end));
			index++;
		}
	}


	private void extractNumberChars(){
		for(int i=0; i<featuresTable.length; i++){
			featuresTable[i][0] = (double)sentencesTable.get(i).length();
		}
	}


	private void createStopwords(){
		stopwords = new HashSet<String>();
		stopwords.add("a");
		stopwords.add("about");
		stopwords.add("above");
		stopwords.add("after");
		stopwords.add("again");
		stopwords.add("against");
		stopwords.add("all");
		stopwords.add("am");
		stopwords.add("an");
		stopwords.add("and");
		stopwords.add("any");
		stopwords.add("are");
		stopwords.add("as");
		stopwords.add("at");
		stopwords.add("be");
		stopwords.add("because");
		stopwords.add("been");
		stopwords.add("before");
		stopwords.add("being");
		stopwords.add("below");
		stopwords.add("between");
		stopwords.add("both");
		stopwords.add("but");
		stopwords.add("by");
		stopwords.add("can");
		stopwords.add("did");
		stopwords.add("do");
		stopwords.add("does");
		stopwords.add("doing");
		stopwords.add("don");
		stopwords.add("down");
		stopwords.add("during");
		stopwords.add("each");
		stopwords.add("few");
		stopwords.add("for");
		stopwords.add("from");
		stopwords.add("further");
		stopwords.add("had");
		stopwords.add("has");
		stopwords.add("have");
		stopwords.add("having");
		stopwords.add("he");
		stopwords.add("her");
		stopwords.add("here");
		stopwords.add("hers");
		stopwords.add("herself");
		stopwords.add("him");
		stopwords.add("himself");
		stopwords.add("his");
		stopwords.add("how");
		stopwords.add("i");
		stopwords.add("if");
		stopwords.add("in");
		stopwords.add("into");
		stopwords.add("is");
		stopwords.add("it");
		stopwords.add("its");
		stopwords.add("itself");
		stopwords.add("just");
		stopwords.add("me");
		stopwords.add("more");
		stopwords.add("most");
		stopwords.add("my");
		stopwords.add("myself");
		stopwords.add("no");
		stopwords.add("nor");
		stopwords.add("not");
		stopwords.add("now");
		stopwords.add("of");
		stopwords.add("off");
		stopwords.add("on");
		stopwords.add("once");
		stopwords.add("only");
		stopwords.add("or");
		stopwords.add("other");
		stopwords.add("our");
		stopwords.add("ours");
		stopwords.add("ourselves");
		stopwords.add("out");
		stopwords.add("over");
		stopwords.add("own");
		stopwords.add("s");
		stopwords.add("same");
		stopwords.add("she");
		stopwords.add("should");
		stopwords.add("so");
		stopwords.add("some");
		stopwords.add("such");
		stopwords.add("t");
		stopwords.add("than");
		stopwords.add("that");
		stopwords.add("the");
		stopwords.add("their");
		stopwords.add("theirs");
		stopwords.add("them");
		stopwords.add("themselves");
		stopwords.add("then");
		stopwords.add("there");
		stopwords.add("these");
		stopwords.add("they");
		stopwords.add("this");
		stopwords.add("those");
		stopwords.add("through");
		stopwords.add("to");
		stopwords.add("too");
		stopwords.add("under");
		stopwords.add("until");
		stopwords.add("up");
		stopwords.add("very");
		stopwords.add("you");
		stopwords.add("your");
		stopwords.add("yours");
		stopwords.add("yourself");
		stopwords.add("was");
		stopwords.add("we");
		stopwords.add("were");
		stopwords.add("what");
		stopwords.add("when");
		stopwords.add("where");
		stopwords.add("which");
		stopwords.add("while");
		stopwords.add("who");
		stopwords.add("whom");
		stopwords.add("why");
		stopwords.add("will");
		stopwords.add("with");
	}


	private String removePunctuation(String stringToRemoveEnding, String endingChar){
		String[] ws = stringToRemoveEnding.split(".");
		String w = "";
		for(int i = 0; i<ws.length; i++){
			w += ws[i];
		}
		return w;
	}

	
	private String normalizeWord(String wordToNormalize){
		while(wordToNormalize.endsWith(".") || wordToNormalize.endsWith(",") || wordToNormalize.endsWith("?") || wordToNormalize.endsWith("!") || 
				wordToNormalize.endsWith(":") || wordToNormalize.endsWith(";") || wordToNormalize.endsWith("(") || wordToNormalize.endsWith(")") ||
				wordToNormalize.endsWith("[") || wordToNormalize.endsWith("]") || wordToNormalize.endsWith("{") || wordToNormalize.endsWith("}")){
			if(wordToNormalize.endsWith(".")){		
				wordToNormalize = removePunctuation(wordToNormalize, ".");
			} else if(wordToNormalize.endsWith(",")){
				wordToNormalize = removePunctuation(wordToNormalize, ",");
			} else if(wordToNormalize.endsWith("?")){
				wordToNormalize = removePunctuation(wordToNormalize, "?");
			} else if(wordToNormalize.endsWith("!")){
				wordToNormalize = removePunctuation(wordToNormalize, "!");
			} else if(wordToNormalize.endsWith(":")){
				wordToNormalize = removePunctuation(wordToNormalize, ":");
			} else if(wordToNormalize.endsWith(";")){
				wordToNormalize = removePunctuation(wordToNormalize, ";");
			} else if(wordToNormalize.endsWith("(")){
				wordToNormalize = removePunctuation(wordToNormalize, "(");
			} else if(wordToNormalize.endsWith(")")){
				wordToNormalize = removePunctuation(wordToNormalize, ")");
			} else if(wordToNormalize.endsWith("[")){
				wordToNormalize = removePunctuation(wordToNormalize, "[");
			} else if(wordToNormalize.endsWith("]")){
				wordToNormalize = removePunctuation(wordToNormalize, "]");
			} else if(wordToNormalize.endsWith("{")){
				wordToNormalize = removePunctuation(wordToNormalize, "{");
			} else if(wordToNormalize.endsWith("}")){
				wordToNormalize = removePunctuation(wordToNormalize, "}");
			}
		}
		return wordToNormalize;
	}


	private void extractNumberStopwordsNorm(){
		for(int i=0; i<sentencesTable.size(); i++){
			String[] words = sentencesTable.get(i).split(" ");
			int numStopws = 0;
			for(int j=0; j<words.length; j++){
				words[j] = normalizeWord(words[j].trim());
				if(stopwords.contains(words[j])){
					numStopws++;
				}
			}
			featuresTable[i][1] = (double)numStopws/(double)words.length;
		}
	}

	private void extractNumberVerbsNorm(){
		// TODO
	}

	private void extractRelativePosNorm(){
		for(int i=0; i<sentencesTable.size(); i++){
			featuresTable[i][3] = (double)i/(double)sentencesTable.size();
		}
	}

	private void extractSubjectWordsNorm(){
		String[] subj = subject.split(" ");
		for(int k=0; k<subj.length; k++)
			subj[k] = normalizeWord(subj[k].trim());
		for(int i=0; i<sentencesTable.size(); i++){
			String[] sent = sentencesTable.get(i).split(" ");
			int subjwords = 0;
			for(int j=0; j<sent.length; j++){
				sent[j] = normalizeWord(sent[j].trim());
				for(int s=0; s<subj.length; s++){
					if(sent[j].equals(subj[s]))
						subjwords++;
				}
			}
			featuresTable[i][4] = (double)subjwords/(double)sent.length;
		}
	}
	
	
	private void determineRelevance(){
		for(int i=0; i<featuresTable.length;i++){
			if(featuresTable[i][1] > 0.142857){
				if(featuresTable[i][4] > 0.045455){
					if(featuresTable[i][1] > 0.484848){
						if(featuresTable[i][3] > 0.633333){
							featuresTable[i][5] = 0.0;
						} else {
							featuresTable[i][5] = 1.0;
						}
					} else {
						featuresTable[i][5] = 1.0;
					}
				} else {
					
					if(featuresTable[i][3] > 0.756757){
						featuresTable[i][5] = 0.0;
					} else {
						if(featuresTable[i][1] > 0.555556){
							if(featuresTable[i][3] > 0.45){
								featuresTable[i][5] = 0.0;
							} else {
								if(featuresTable[i][3] > 0.153856){
									featuresTable[i][5] = 1.0;
								} else {
									featuresTable[i][5] = 0.0;
								}
							}
						} else {
							featuresTable[i][5] = 1.0;
						}
					}
					
				}	
			} else {
				if(featuresTable[i][2] > 0.153846){
					if(featuresTable[i][0] > 40){
						featuresTable[i][5] = 0.0;
					} else {
						if(featuresTable[i][2] > 0.4){
							featuresTable[i][5] = 0.0;
						} else {
							if(featuresTable[i][3] > 0.653061){
								featuresTable[i][5] = 0.0;
							} else {
								featuresTable[i][5] = 1.0;
							}
						}
					}
				} else {
					featuresTable[i][5] = 0.0;
				}
			}
		}
		
	}
	

	// Col 0: chars, Col 1: num_stopw_norm, Col 2: num_verbs_norm, Col 3: rel_pos_norm, Col 4: subj_words_norm , Col 5: relevant
	private void createFeaturesTable(){
		featuresTable = new double[sentencesTable.size()][6];
		extractNumberChars();
		extractNumberStopwordsNorm();
		extractRelativePosNorm();
		for(int i = 0; i<sentencesTable.size(); i++){
			System.out.println("KEY: " + i + ", VALUE: " + sentencesTable.get(i));
			System.out.println("CHARS: " + featuresTable[i][0]);
			System.out.println("STOPWORDS_NORM: " + featuresTable[i][1]);
			System.out.println("REL_POS_NORM: " + featuresTable[i][3]);
			System.out.println("SUBJ_WORDS_NORM: " + featuresTable[i][4] + "\n");
		}
	}




}
