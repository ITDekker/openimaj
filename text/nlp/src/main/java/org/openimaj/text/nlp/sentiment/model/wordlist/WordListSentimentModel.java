package org.openimaj.text.nlp.sentiment.model.wordlist;

import java.util.List;

import org.openimaj.experiment.dataset.Dataset;
import org.openimaj.ml.annotation.Annotated;
import org.openimaj.ml.annotation.Annotator;
import org.openimaj.ml.annotation.IdentityFeatureExtractor;
import org.openimaj.text.nlp.sentiment.model.SentimentModel;
import org.openimaj.text.nlp.sentiment.type.DiscreteCountBipolarSentiment;
import org.openimaj.text.nlp.sentiment.type.Sentiment;
import org.openimaj.util.pair.IndependentPair;

/**
 * A {@link SentimentModel} which loads an MPQA sentiment wordlist and ascribes {@link DiscreteCountBipolarSentiment} sentiments.
 * 
 * That is to say, for each list of strings, a count of words of the three sentiments is made as well as a total count of words.
 * 
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 * @param <S> the sentiment this model learns and returns
 * @param <T> the {@link WordListSentimentModel} type to be returned and cloned etc
 *
 */
public abstract class WordListSentimentModel<S extends Sentiment,T extends WordListSentimentModel<S,T>> extends SentimentModel<S,T>{

	/**
	 * see: {@link Annotator}
	 * @param extractor
	 */
	public WordListSentimentModel() {
		super(new IdentityFeatureExtractor<List<String>>());
	}

	@Override
	public int numItemsToEstimate() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public abstract T clone();

	@Override
	public void estimate(List<? extends IndependentPair<List<String>, S>> data) {
		
	}

	@Override
	public boolean validate(IndependentPair<List<String>, S> data) {
		S predicted = predict(data.firstObject());
		
		return predicted.equals(data.secondObject());
	}

	@Override
	public abstract S predict(List<String> data);

	@Override
	public double calculateError(List<? extends IndependentPair<List<String>, S>> data) {
		double correct = 0;
		double total = 0;
		for (IndependentPair<List<String>, S> independentPair : data) {
			if(validate(independentPair)){
				correct++;
			}
			total++;
		}
		return 1 - (correct/total);
	}

}