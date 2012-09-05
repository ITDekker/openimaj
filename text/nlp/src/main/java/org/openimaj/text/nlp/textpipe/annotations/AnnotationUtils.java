package org.openimaj.text.nlp.textpipe.annotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openimaj.text.nlp.textpipe.annotations.POSAnnotation.PartOfSpeech;

public class AnnotationUtils {
	
	/**
	 * Get the string tokens from a list of {@link TokenAnnotation}s
	 * @param tokens
	 * @return List of string tokens
	 */
	public static List<String> getStringTokensFromTokenAnnotationList(List<TokenAnnotation> tokens){
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator iterator = tokens.iterator(); iterator.hasNext();) {
			TokenAnnotation tokenAnnotation = (TokenAnnotation) iterator.next();
			result.add(tokenAnnotation.stringToken);
		}
		return result;
	}
	
	public static List<String> getStringPOSsFromTokenAnnotationList(List<TokenAnnotation> tokens){
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator iterator = tokens.iterator(); iterator.hasNext();) {
			TokenAnnotation tokenAnnotation = (TokenAnnotation) iterator.next();
			POSAnnotation pos = tokenAnnotation.getAnnotationsFor(POSAnnotation.class).get(0);
			if(pos.equals(PartOfSpeech.UK)){
				result.add(tokenAnnotation.stringToken);
			}
			else result.add(tokenAnnotation.getAnnotationsFor(POSAnnotation.class).get(0).toString());
		}
		return result;
	}
	
	public  static String[] ListToArray(List<String> convert){
		String[] result = new String[convert.size()];
		for (int i = 0; i < convert.size(); i++) {
			result[i]=convert.get(i);
		}
		return result;
	}
	
	/**
	 * Check if a {@link TextPipeAnnotation} has been added to all members in a list of {@link TextPipeAnnotation}.
	 * @param toBeChecked
	 * @param check
	 * @return false if not all members have check annotation.
	 */
	public static boolean allHaveAnnotation(List<? extends TextPipeAnnotation> toBeChecked,Class<? extends TextPipeAnnotation> check){		
		for(TextPipeAnnotation anno: toBeChecked){
			if(!anno.getAnnotationKeyList().contains(check))return false;
		}
		return true;	
	}
}
