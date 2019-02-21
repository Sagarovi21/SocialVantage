package com.navishkaar.service;

import com.navishkaar.core.EbayResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class EBaySearchService {
	@Value("${ebay.appName}")
	private String appName;

	public List<EbayResponse> search(String searchTerm) throws UnirestException, UnsupportedEncodingException {
		List<EbayResponse> results = new ArrayList<>();
		HttpResponse<String> response = Unirest.get(String.format(
				"https://svcs.ebay.com/services/search/FindingService/v1?SECURITY-APPNAME=%s&OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD=&keywords=%s&paginationInput.entriesPerPage=%d&GLOBAL-ID=EBAY-IN&siteid=203",
				appName.trim(), URLEncoder.encode(searchTerm, "UTF-8"), 10)).header("cache-control", "no-cache")
				.header("Postman-Token", "0d2ca9a4-a0ad-4247-a85d-359ac8332514").asString();
		JSONArray itemsJsonArray = new JSONObject(response.getBody()).getJSONArray("findItemsByKeywordsResponse")
				.getJSONObject(0)
				.getJSONArray("searchResult")
				.getJSONObject(0)
				.getJSONArray("item");
		for (int i = 0; i < itemsJsonArray.length(); i++) {
			JSONObject jsonItem = itemsJsonArray.getJSONObject(i);
			EbayResponse item = new EbayResponse();
			item.setTitle(jsonItem.getJSONArray("title").getString(0));
			item.setViewItemURL(jsonItem.getJSONArray("viewItemURL").getString(0));
			item.setGalleryURL(jsonItem.getJSONArray("galleryURL").getString(0));
			results.add(item);
		}

		return results;
	}

}
