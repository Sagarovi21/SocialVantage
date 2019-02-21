package com.example.demo.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkerTest {

	@Autowired
	private Worker worker;

	@Test
	public void testDowork() {

		worker.dowork("https://gadgets.ndtv.com/mobiles/smartphones");
		worker.dowork("https://www.digit.in/top-products/top-10-smartphones-to-buy-in-india-1.html");
		worker.dowork("https://shop.gadgetsnow.com/smartphones/");
		worker.dowork("https://www.consumerreports.org/products/smart-phone/ratings-overview/");
	}

}
