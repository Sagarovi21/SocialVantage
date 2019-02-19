package com.navishkaar.service;

import java.util.List;
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

	public static Twitter getTwitterinstance() {
		if (twitter != null)
			return twitter;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("JZupFU1LR8DVnH1CVPWEsCP2z")
				.setOAuthConsumerSecret("IjQQPYOQceDYT70nf4IrRZ0VSjM9LAtyORHwblX20xbuXk1epi")
				.setOAuthAccessToken("301682821-EFsP8uSCvuYk4CvoHTmCLrh7BK7kQxCMg8LkGzhj")
				.setOAuthAccessTokenSecret("EFUmjoUU1AQs0SI7VE7WsweeVUg7UN4goTDabrK9VsQRc");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		return twitter;

	}

	public List<Status> search(String searchTerm) throws TwitterException {

		Twitter twitter = getTwitterinstance();
		Query query = new Query(searchTerm);
		query.setLang("en");
		query.setCount(100);
		QueryResult result = twitter.search(query);

		return result.getTweets();
		/*
		 * .stream() .map(item -> item.getText()) .collect(Collectors.toList());
		 */
	}

}
