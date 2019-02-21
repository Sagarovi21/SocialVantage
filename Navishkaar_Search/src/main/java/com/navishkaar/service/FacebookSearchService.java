package com.navishkaar.service;

import org.springframework.beans.factory.annotation.Value;
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
	@Value("${facebook.appId}")
	private String appId;
	@Value("${facebook.appSecret}")
	private String appSecret;
	@Value("${facebook.accessToken}")
	private String accessToken;
	@Value("${facebook.permissions}")
	private String permissions;
	

	private Facebook getFacebookinstance() {
		if (facebook != null)
			return facebook;
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthAppId(appId.trim())
				.setOAuthAppSecret(appSecret.trim())
				.setOAuthAccessToken(accessToken.trim())
				.setOAuthPermissions(permissions.trim());
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
