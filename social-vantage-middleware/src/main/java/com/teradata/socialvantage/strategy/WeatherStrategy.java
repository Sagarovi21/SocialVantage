package com.teradata.socialvantage.strategy;

import org.springframework.stereotype.Component;

import com.teradata.socialvantage.payload.Input;
import com.teradata.socialvantage.payload.Output;

@Component("weather")
public class WeatherStrategy implements Processor {

	@Override
	public Output process(Input input) {
		return null;
	}

}
