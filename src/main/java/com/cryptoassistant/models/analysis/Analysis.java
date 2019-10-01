package com.cryptoassistant.models.analysis;

public class Analysis {

	private double currentPrice;
	private double sma;
	private double standardDeviation;
	private double upperBollingerBand;
	private double lowerBollingerBand;
	private double rsi;
	private double macd;
	private double twelveDayEMA;
	private double twelveDayEMAMult = (double) 2/(12+1);
	private double twentysixDayEMA;
	private double twentysixDayEMAMult = (double) 2/(26+1);
	private double nineDayEMA;
	private double nineDayEMAMult = (double) 2/(9+1);
	private double macdHistogramValue;
	private String decisionText;
	private Boolean isWorthy = false;
	

	public double getSma() {
		return sma;
	}

	public void setSma(double sma) {
		this.sma = sma;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public double getUpperBollingerBand() {
		String str = String.format("%1.2f", upperBollingerBand);
		return Double.valueOf(str);
	}

	public void setUpperBollingerBand(double upperBollingerBand) {
		this.upperBollingerBand = upperBollingerBand;
	}

	public double getLowerBollingerBand() {
		String str = String.format("%1.2f", lowerBollingerBand);
		return Double.valueOf(str);
	}

	public void setLowerBollingerBand(double lowerBollingerBand) {
		this.lowerBollingerBand = lowerBollingerBand;
	}

	public double getRsi() {
		String str = String.format("%1.2f", rsi);
		return Double.valueOf(str);
	}

	public void setRsi(double rsi) {
		this.rsi = rsi;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getMacd() {
		String str = String.format("%1.2f", macd);
		return Double.valueOf(str);
	}

	public void setMacd(double macd) {
		this.macd = macd;
	}

	public double getTwelveDayEMA() {
		return twelveDayEMA;
	}

	public void setTwelveDayEMA(double twelveDayEMA) {
		this.twelveDayEMA = twelveDayEMA;
	}

	public double getTwelveDayEMAMult() {
		return twelveDayEMAMult;
	}

	public void setTwelveDayEMAMult(double twelveDayEMAMult) {
		this.twelveDayEMAMult = twelveDayEMAMult;
	}

	public double getTwentysixDayEMA() {
		return twentysixDayEMA;
	}

	public void setTwentysixDayEMA(double twentysixDayEMA) {
		this.twentysixDayEMA = twentysixDayEMA;
	}

	public double getTwentysixDayEMAMult() {
		return twentysixDayEMAMult;
	}

	public void setTwentysixDayEMAMult(double twentysixDayEMAMult) {
		this.twentysixDayEMAMult = twentysixDayEMAMult;
	}

	public double getNineDayEMA() {
		String str = String.format("%1.2f", nineDayEMA);
		return Double.valueOf(str);
	}

	public void setNineDayEMA(double nineDayEMA) {
		this.nineDayEMA = nineDayEMA;
	}

	public double getNineDayEMAMult() {
		return nineDayEMAMult;
	}

	public void setNineDayEMAMult(double nineDayEMAMult) {
		this.nineDayEMAMult = nineDayEMAMult;
	}

	public double getMacdHistogramValue() {
		String str = String.format("%1.2f", macdHistogramValue);
		return Double.valueOf(str);
	}

	public void setMacdHistogramValue(double macdHistogramValue) {
		this.macdHistogramValue = macdHistogramValue;
	}

	public String getDecisionText() {
		return decisionText;
	}

	public void setDecisionText(String decisionText) {
		this.decisionText = decisionText;
	}

	public Boolean isWorthy() {
		return isWorthy;
	}

	public void setIsWorthy(Boolean isWorthy) {
		this.isWorthy = isWorthy;
	}
	
}
