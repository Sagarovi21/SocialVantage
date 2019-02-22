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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Reviews;
import com.example.demo.entity.TaskStatus;
import com.example.demo.jpa.ReviewRepository;
import com.example.demo.jpa.TaskStatusRepository;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, 
proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Worker {

	private static Logger logger = LoggerFactory.getLogger(Worker.class);
	

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private TaskStatusRepository taskStatusRepository;
	
	private void updateTaskStatus(int taskId, String searchTerm, int pagesize,int index, String imageUrl) {
		String task ="Google_Crawl";
		String status = "Submit";
		TaskStatus taskStatus = new TaskStatus(taskId, task, status);
		taskStatus.setSearch(searchTerm);
		taskStatus.setIndex(index);
		taskStatus.setImageUrl(imageUrl);
		taskStatus.setPagesFound(pagesize);
		taskStatusRepository.save(taskStatus);
	}

	public void dowork(String url,String imageUrl, int taskId, String searchString, int index) {
		logger.info("URL :" + url);
		Map<String, List<String>> map = new HashMap<>();
		try {
			getAllLinks(url, map);
		} catch (HttpStatusException ex) {
			logger.error("HttpStatusException in url parsing", ex);
		} catch (IOException e) {
			logger.error("IOException in url parsing", e);
		}
		int counter = 0;
		for(Map.Entry<String,List<String>> entry: map.entrySet()) {
			runFromAnotherThreadPool(entry.getKey(), entry.getValue(), taskId, searchString,  index,counter);
			counter++;
		}
		
		updateTaskStatus(taskId,searchString,map.size(),index,imageUrl);
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
	public void runFromAnotherThreadPool(String url, List<String> links, int taskId, String searchString, int index, int subindex) {
		logger.info("Execute method asynchronously with configured executor" + Thread.currentThread().getName());
		ExecutorService executor = Executors.newFixedThreadPool(120);
		List<Future<List<Reviews>>> list = new ArrayList<Future<List<Reviews>>>();
		links.forEach((link) -> {
			Future<List<Reviews>> future = executor.submit(new PageExtractor(url,link));
			list.add(future);
			 
		});
		
		for(Future<List<Reviews>> fut : list){ persistReviews(fut, taskId, searchString, index,  subindex); }
		 
		
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			logger.error("failed for shutdown", e);
		}

	}

	@Transactional
	private void persistReviews(Future<List<Reviews>> fut, int taskId, String searchString,int index,int subindex) {
		try {
			List<Reviews> reviews= fut.get();
			if(reviews !=null && reviews.size() > 0) {
			reviews.forEach(review -> { 
						review.setTaskId(taskId);
						review.setCategory(searchString);
						logger.info(review.toString()); 
						
					});
			reviewRepository.saveAll(reviews);
			updateTaskStatusIdxCmnts(taskId,searchString,index,subindex,reviews.size());
			}
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}
	}

	private void updateTaskStatusIdxCmnts(int taskId, String searchString, int index, int subindex, int size) {
		String task ="Google_Crawl";
		String status = "Done";
		TaskStatus taskStatus = new TaskStatus(taskId, task, status);
		taskStatus.setSearch(searchString);
		taskStatus.setSearch(searchString);
		taskStatus.setIndex(index);
		taskStatus.setSubIndex(subindex);
		taskStatus.setCommentsFound(size);
		taskStatusRepository.save(taskStatus);// TODO Auto-generated method stub
		
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
