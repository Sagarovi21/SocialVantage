package com.navishkaar.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterSearchService {

	private static Twitter twitter = null;
	@Value("${twitter.consumerKey}")
	private String consumerKey;
	@Value("${twitter.consumerSecret}")
	private String consumerSecret;
	@Value("${twitter.accessToken}")
	private String accessToken;
	@Value("${twitter.accessTokenSecret}")
	private String accessTokenSecret;
	
	public Twitter getTwitterinstance() {
		if (twitter != null)
			return twitter;
		System.out.println("count");
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey.trim())
				.setOAuthConsumerSecret(consumerSecret.trim())
				.setOAuthAccessToken(accessToken.trim())
				.setOAuthAccessTokenSecret(accessTokenSecret.trim());
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		return twitter;

	}

	public List<String> search(String searchTerm) throws TwitterException {

		Twitter twitter = getTwitterinstance();
		Query query = new Query(searchTerm);
		query.setLang("en");
		query.setCount(100);
		QueryResult result = twitter.search(query);
		return result.getTweets()
		  .stream() .map(item -> item.getText()) .collect(Collectors.toList());
		 
	}

}
