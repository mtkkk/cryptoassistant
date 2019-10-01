package com.cryptoassistant.models.cryptowatch;

import java.util.List;

public class CandleStickHolder {

	private List<CandleStick> onehour;
	private List<CandleStick> twohour;
	private List<CandleStick> fourhour;
	private List<CandleStick> oneDay;
	
	public List<CandleStick> getOnehour() {
		return onehour;
	}
	public void setOnehour(List<CandleStick> onehour) {
		this.onehour = onehour;
	}
	public List<CandleStick> getTwohour() {
		return twohour;
	}
	public void setTwohour(List<CandleStick> twohour) {
		this.twohour = twohour;
	}
	public List<CandleStick> getFourhour() {
		return fourhour;
	}
	public void setFourhour(List<CandleStick> fourhour) {
		this.fourhour = fourhour;
	}
	public List<CandleStick> getOneDay() {
		return oneDay;
	}
	public void setOneDay(List<CandleStick> oneDay) {
		this.oneDay = oneDay;
	}
	
}
