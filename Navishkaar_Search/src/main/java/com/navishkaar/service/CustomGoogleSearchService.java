package com.navishkaar.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.navishkaar.core.CustomResult;
import com.navishkaar.core.Image;

@Service
public class CustomGoogleSearchService {
	
	private static Logger logger = LoggerFactory.getLogger(CustomGoogleSearchService.class);
	
	@Autowired
	CustomGoogleSearchEngine customGoogleSearchEngine;

	private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
	private static int num = 100;

	public Map<String, String> search(String searchTerm) throws IOException {
		logger.info("Search: "+searchTerm);
		Map<String, String> searchResults = new HashMap<>();
		String searchURL = GOOGLE_SEARCH_URL + "?q=" + searchTerm + "&num=" + num;
		Document doc = Jsoup.connect(searchURL).userAgent(
				  "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
				.timeout(5000).get();
		Elements results = doc.select("h3.r > a");

		for (Element result : results) {
			String linkHref = result.absUrl("href");
			linkHref = URLDecoder.decode(linkHref.substring(linkHref.indexOf('=') + 1, linkHref.indexOf('&')), "UTF-8");
			String linkText = result.text();
			searchResults.put(linkText, linkHref);
		}
		return searchResults;
	}

	public String searchGoogleCustom1(String searchTerm) throws Exception {
		String output = customGoogleSearchEngine.search(searchTerm, 1);
		return output;
	}
	
	
	public List<CustomResult> searchGoogleCustom(String searchTerm) throws Exception {
		List<CustomResult> resultList = new ArrayList<>(100);
		String output = customGoogleSearchEngine.search(searchTerm, 1);
		JSONObject obj = new JSONObject(output);
		try {
			while (obj.getString("kind") != null) {
				int nextStart = obj.getJSONObject("queries").getJSONArray("nextPage").getJSONObject(0)
						.getInt("startIndex");
				JSONArray jsonArray = obj.getJSONArray("items");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject temp = jsonArray.getJSONObject(i);
					CustomResult result = new CustomResult();
					result.setTitle(temp.getString("title"));
					result.setUrl(temp.getString("link"));
					try {
						JSONArray array;
						if ((array = temp.getJSONObject("pagemap").getJSONArray("cse_thumbnail")) != null) {
							JSONObject imageJsonObject = array.getJSONObject(0);
							Image image = new Image();
							image.setHeight(imageJsonObject.getString("height"));
							image.setWidth(imageJsonObject.getString("width"));
							image.setSrc(imageJsonObject.getString("src"));
							result.setImage(image);
						}
					} catch (JSONException e) {

					}
					resultList.add(result);
				}
				output = customGoogleSearchEngine.search(searchTerm, nextStart);
				if (output == null)
					break;
				obj = new JSONObject(output);
			}
		} catch (JSONException e) {

		}
		return resultList;
	}
}
