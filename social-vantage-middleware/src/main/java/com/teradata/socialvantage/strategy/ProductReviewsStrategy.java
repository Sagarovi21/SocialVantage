package com.teradata.socialvantage.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.teradata.socialvantage.payload.DataInputRequest;
import com.teradata.socialvantage.payload.Input;
import com.teradata.socialvantage.payload.Output;
import com.teradata.socialvantage.payload.Result;
import com.teradata.socialvantage.payload.ResultOutputResponse;

@Component("reviews")
public class ProductReviewsStrategy implements Processor {


	@Override
	public Output process(Input input) {
		DataInputRequest request = (DataInputRequest) input;
		Result r1 = new Result("Redme 1", 9.9, 1);
		Result r2 = new Result("1 plus", 9.8, 2);
		Result r3 = new Result("Moto", 8.9, 3);
		Result r4 = new Result("Note", 8.8, 4);
		List<Result> results = new ArrayList<>();
		results.add(r1);
		results.add(r2);
		results.add(r3);
		results.add(r4);
		return new ResultOutputResponse(request, results);
	}

}
