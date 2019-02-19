package com.navishkaar.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class GoogleSearchService {

	private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
	private static int num = 100;

	public Map<String, String> search(String searchTerm) throws IOException {
		Map<String, String> searchResults = new HashMap<>();
		String searchURL = GOOGLE_SEARCH_URL + "?q=" + searchTerm + "&num=" + num;
		Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
		Elements results = doc.select("h3.r > a");
		
		for (Element result : results) {
			String linkHref = result.absUrl("href");
			linkHref = URLDecoder.decode(linkHref.substring(linkHref.indexOf('=') + 1, linkHref.indexOf('&')), "UTF-8");
			String linkText = result.text();
			searchResults.put(linkText, linkHref);
		}
		return searchResults;
	}
}
