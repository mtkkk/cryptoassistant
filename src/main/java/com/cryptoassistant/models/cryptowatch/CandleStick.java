package com.cryptoassistant.models.cryptowatch;

public class CandleStick {

	private long closeTime;
	private double openPrice;
	private double highPrice;
	private double lowPrice;
	private double closePrice;
	private double volume;
	
	public CandleStick() {}
	
	public CandleStick(double[] values) {
		this.closeTime = (long) values[0];
		this.openPrice = values[1];
		this.highPrice = values[2];
		this.lowPrice = values[3];
		this.closePrice = values[4];
		this.volume = values[5];
	}
	
	public long getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}
	public double getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}
	public double getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}
	public double getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
}
