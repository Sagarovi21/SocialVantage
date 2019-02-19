package com.navishkaar.service;

import org.springframework.stereotype.Service;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.conf.ConfigurationBuilder;

@Service
public class FacebookSearchService {
	private static Facebook facebook = null;

	private static Facebook getFacebookinstance() {
		if (facebook != null)
			return facebook;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthAppId("2020182234762214")
				.setOAuthAppSecret("4ea82168785772eeb11e82d92f5be67d")
				.setOAuthAccessToken("2288866aa6362d30e647115e5a45d702")
				.setOAuthPermissions("email,publish_stream,...");
		FacebookFactory ff = new FacebookFactory(cb.build());
		facebook = ff.getInstance();
		return facebook;
	}
	
	public ResponseList<Post> search(String searchTerm) throws FacebookException {
		  
		Facebook facebook = getFacebookinstance();
		ResponseList<Post> results = facebook.searchPosts(searchTerm,new Reading().limit(100));
		return results;
	    
	}
}
