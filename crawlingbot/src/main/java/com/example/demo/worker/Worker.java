package com.example.demo.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Reviews;
import com.example.demo.jpa.ReviewRepository;

@Component
public class Worker {

	private static Logger logger = LoggerFactory.getLogger(Worker.class);
	

	@Autowired
	private ReviewRepository reviewRepository;
	
	

	public void dowork(String url) {
		logger.info("URL :" + url);
		Map<String, List<String>> map = new HashMap<>();
		try {
			getAllLinks(url, map);
		} catch (HttpStatusException ex) {
			logger.error("HttpStatusException in url parsing", ex);
		} catch (IOException e) {
			logger.error("IOException in url parsing", e);
		}
		map.forEach((k, v) -> {
			runFromAnotherThreadPool(k, v);
		});
		logger.info(
				"##################### Excution Initiated ###########################################################");
	}

	private void getAllLinks(String url, Map<String, List<String>> map) throws IOException {
		Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5000).get();
		List<String> links = new ArrayList();
		Elements elems = doc.select("a:has(img)");
		elems.forEach((ele) -> {
			links.add(ele.attr("href"));
		});
		if(links.size()==0) {
			elems = doc.select("a");
			elems.forEach((el) -> {
				Elements e = el.parents().first().select(":has(img)");
				if(e != null && e.size() > 0) {
					links.add(el.attr("href"));
				}
			});
		}

		map.put(url, links);
	}

	@Async("threadPoolTaskExecutor")
	public void runFromAnotherThreadPool(String url, List<String> links) {
		logger.info("Execute method asynchronously with configured executor" + Thread.currentThread().getName());
		ExecutorService executor = Executors.newFixedThreadPool(50);
		List<Future<List<Reviews>>> list = new ArrayList<Future<List<Reviews>>>();
		links.forEach((link) -> {
			Future<List<Reviews>> future = executor.submit(new PageExtractor(url,link));
			try {
				logger.info("&&&&&&&&&&&&&&&&&&&&"+future.get().size());
			} catch (InterruptedException | ExecutionException e) {
				logger.error("error during extract final calls",e);
			}
			list.add(future);
		});
		
		 for(Future<List<Reviews>> fut : list){ persistReviews(fut); }
		
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			logger.error("failed for shutdown", e);
		}

	}

	@Transactional
	private void persistReviews(Future<List<Reviews>> fut) {
		try {
			fut.get().forEach(review -> { logger.info(review.toString()); reviewRepository.save(review);});
			
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Worker worker = new Worker();
		Map<String, List<String>> map = new HashMap<>();
		String url1 = "https://gadgets.ndtv.com/mobiles/smartphones";
		String url2 = "https://www.digit.in/top-products/top-10-smartphones-to-buy-in-india-1.html";
		worker.getAllLinks(url1, map);
		map.get(url1).forEach((link) -> {
			logger.info(link);
		});
	}
}
