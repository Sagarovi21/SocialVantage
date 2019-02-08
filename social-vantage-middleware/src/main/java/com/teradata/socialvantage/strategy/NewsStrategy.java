package com.teradata.socialvantage.strategy;

import org.springframework.stereotype.Component;

import com.teradata.socialvantage.payload.Input;
import com.teradata.socialvantage.payload.Output;

@Component("news")
public class NewsStrategy implements Processor {

	@Override
	public Output process(Input input) {
		return null;
	}

}
