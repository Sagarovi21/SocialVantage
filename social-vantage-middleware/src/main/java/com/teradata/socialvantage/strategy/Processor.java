package com.teradata.socialvantage.strategy;

import com.teradata.socialvantage.payload.Input;
import com.teradata.socialvantage.payload.Output;

public interface Processor {
	
	Output process(Input input);
	
}
