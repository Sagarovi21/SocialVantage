package com.example.demo.worker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Reviews;
import com.example.demo.model.ResultData;
import com.example.demo.model.Review;

@Component
@Scope("prototype")
public class PageExtractor implements Callable<List<Reviews>>{

	private static Logger logger = LoggerFactory.getLogger(Runnable.class);
	private String url;
	private String parent;
	private List<Reviews> result;
	
	
	public PageExtractor(String parent, String url) {
		super();
		this.url = url;
		this.parent = parent;
	}

	
	public List<Reviews> getResult() {
		return result;
	}



	public void setResult(List<Reviews> result) {
		this.result = result;
	}



	public String getParent() {
		return parent;
	}


	public void setParent(String parent) {
		this.parent = parent;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

	public void init() {
		URI uri;
		try {
			uri = new URI(parent);
			String hostname = uri.getHost();
			String protocol = uri.getScheme();
			if( !url.contains("http")) {
				this.setUrl(protocol+"://"+hostname+this.url);
			}
		} catch (URISyntaxException e) {
			logger.error("error during url extraction", e);
		}
	    
		
	}
	
	
	public void run() {
		init();
		logger.info("Child :"+this.url);
		try {
			Document doc = Jsoup.connect(this.url).userAgent("Mozilla").timeout(5000).get();
			String title = doc.select("h1").text();
			String price = null;
			price = extractPrice(doc, price);
			List<String> specs = new ArrayList<>();
			List<Review> list = new ArrayList<>();
			getFeatures(doc, specs);
			String feature = String.join(" | ", specs);
			logger.info("feature : "+feature);
			List<Pattern> rating_Patterns = new ArrayList<>();
			rating_Patterns.add(Pattern.compile("(\\d|\\d.\\d) out of (\\d|\\d.\\d)"));
			rating_Patterns.add(Pattern.compile("(\\d|\\d.\\d) Out of (\\d|\\d.\\d)"));
			Elements reviews = doc.select("[class*='reviewList']");
			
			reviews = doc.select("[class*='review']");
			extractReviews( list, rating_Patterns, reviews);
			if(list.size() < 2) {
				reviews = doc.select("[class*='cmnt']");
				extractReviews( list, rating_Patterns, reviews);
			}
			result = new ArrayList<>();
			for(Review review : list) {
				String titleText = title.replaceAll("[^A-Za-z0-9()\\s\\.\\[\\]]", "");
				if(titleText.length() > 130) { titleText = titleText.substring(0, 130);}
				String reviewText = review.getReview().replaceAll("[^A-Za-z0-9()\\s\\.\\[\\]]", "");
				if(reviewText.length() > 990) { reviewText = reviewText.substring(0, 990);}
				feature = feature.replaceAll("[^A-Za-z0-9()\\s\\.\\[\\]]", "");
				if(feature.length() > 230) { feature = feature.substring(0, 230);}
				result.add(new 
						Reviews(1, titleText,"", reviewText, review.getRating(), review.getTotalRating(), (price == null ? 0.0d: Double.parseDouble(price)),
								feature, feature)) ;
						
			
			}
			logger.info("title : "+title+ " price: "+ price);
			
		} catch (HttpStatusException ex) {
			logger.error("HttpStatusException in url parsing", ex);
		} catch (IOException e) {
			logger.error("IOException in url parsing", e);
		}
		
	}


	private void extractReviews(List<Review> list, List<Pattern> rating_Patterns, Elements reviews) {
		for(Element review : reviews) {
			String rating = null;
			String total_rating = null;
			String comments = null;
			//logger.info(review.toString());
			for(Element children : review.children()) {
				//logger.info(children.toString());
				//logger.info(children.text());
				
				for(Pattern pattern : rating_Patterns)
				if (rating == null) {
					Matcher m = pattern.matcher(children.text());
					m = pattern.matcher(children.text()); 
					while (m.find()) { 
						rating = m.group(1); 
						total_rating = m.group(2); 
					}
					if (rating != null) {
						break;
					}
				}
				if (rating == null) {
					int count = 0;
					int rate =0;
					Elements rates = children.select("[class*='star']:not(:has(*))");
					String refer = null;
					for ( Element rele : rates) {
						
						if (refer == null) {
							refer = rele.className();
						}
						if (!(refer.compareTo(rele.className()) > 0)) {
							
							rate +=1;
						}
						count++;
					}
					if(rate != 0 && count != 0 && count <= 10) {
						rating = Integer.toString(rate);
						total_rating = Integer.toString(count);	
					}
				}
				
				if(rating != null) {
					comments += children.text();
				}
			}
			if(rating != null) {
				logger.info(rating +" : "+total_rating+ " : "+comments);
				list.add(new Review(comments,Float.parseFloat(rating),Float.parseFloat(total_rating)));
			}
		} 
		
	}


	private void getFeatures(Document doc, List<String> specs) {
		Elements features = doc.select("div[class*='feature']");
		for(Element feature : features) {
			feature.children().forEach((e)->{specs.add(e.text());});
		}
		if(features == null || features.size() == 0) {
			features = doc.select("div[id*='feature']");
			for(Element feature : features) {
				feature.children().forEach((e)->{ specs.add(e.text());});
			}
		}
		if(features == null || features.size() == 0) {
			features = doc.select("div[class*='specific']");
			for(Element feature : features) {
				feature.children().forEach((e)->{specs.add(e.text());});
			}
		}
		if(features == null || features.size() == 0) {
			features = doc.select("div[id*='specific']");
			for(Element feature : features) {
				feature.children().forEach((e)->{ specs.add(e.text());});
			}
		}
		if(features == null || features.size() == 0) {
			features = doc.select("div[class*='detail']");
			for(Element feature : features.select(":not(:has(*)")) {
				if(feature.hasText()) {
				specs.add(feature.text());
				}
			}
		}
		if(features == null || features.size() == 0) {
			features = doc.select("div[id*='detail']");
			for(Element feature : features.select(":not(:has(*)")) {
				if(feature.hasText()) {
				specs.add(feature.text());
				}
			}
		}
	}


	private String extractPrice(Document doc, String price) {
		Elements eles = doc.select("div[class*='price']");
		for(Element ele : eles) {
			Elements priceEle = ele.getElementsMatchingText("^(0|[1-9][0-9]*)$");
			if(priceEle.size()>0) {
				price = priceEle.first().text();
			}
			priceEle = ele.getElementsMatchingText("^\\d{1,3}(,\\d{3})*(\\.\\d+)?$");
			if(priceEle.size()>0) {
				price = priceEle.first().text().replaceAll(",", "");
			}
		}
		if( price == null) {
			eles = doc.select("div[id*='price']");
			for(Element ele : eles) {
				Elements priceEle = ele.getElementsMatchingText("^(0|[1-9][0-9]*)$");
				if(priceEle.size()>0) {
					price = priceEle.first().text();
				}
				priceEle = ele.getElementsMatchingText("^\\d{1,3}(,\\d{3})*(\\.\\d+)?$");
				if(priceEle.size()>0) {
					price = priceEle.first().text().replaceAll(",", "");
				}
			}
		}
		return price;
	}
	
	public static void main(String[] args){
		String url1 ="https://gadgets.ndtv.com/samsung-galaxy-a9s-5709";
		String url2 ="https://www.digit.in/mobile-phones/xiaomi-mi-a2-price-125589.html";
		String url3 = "https://shop.gadgetsnow.com/smartphones/lenovo-k8-note-64gb-black-4gb-ram-/10021/p_G28690";
		String url4 = "https://gadgets.ndtv.com/vivo-z3i-standard-edition-8950";
		String url5 ="https://gadgets.ndtv.com/samsung-m20-galaxy-8938";
		String url6 ="https://www.consumerreports.org/products/smart-phone/sony-xperia-xz1-394729/overview/";
		PageExtractor pageExtractor = new PageExtractor("https://gadgets.ndtv.com/",url6);
		pageExtractor.run();
	}


	@Override
	public List<Reviews> call() throws Exception {
		run();
		return this.result;
	}

}
