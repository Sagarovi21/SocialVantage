package com.navishkaar.service;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.navishkaar.core.CustomResult;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomGoogleSearchServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(CustomGoogleSearchServiceTest.class);

	@Autowired
	private CustomGoogleSearchService customGoogleSearchService;
	
	@Test
	public void test() {
		try {
			 customGoogleSearchService.searchGoogleCustom("smartphones")
					.forEach((re) -> logger.info(re.toString()));
					
		} catch (Exception e) {
			logger.error("error during custom search",e);
		}
	}

}
