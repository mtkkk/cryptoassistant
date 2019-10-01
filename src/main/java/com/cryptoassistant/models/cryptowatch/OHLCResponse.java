package com.cryptoassistant.models.cryptowatch;

public class OHLCResponse {

	private Result result;
	private Allowance allowance;
	
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public Allowance getAllowance() {
		return allowance;
	}
	public void setAllowance(Allowance allowance) {
		this.allowance = allowance;
	}
}
