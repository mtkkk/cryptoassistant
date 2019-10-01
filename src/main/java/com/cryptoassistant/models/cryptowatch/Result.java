package com.cryptoassistant.models.cryptowatch;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Result {

	@SerializedName("3600")
	private List<double[]> onehour;
	@SerializedName("7200")
	private List<double[]> twohours;
	@SerializedName("14400")
	private List<double[]> fourhours;
	@SerializedName("86400")
	private List<double[]> oneday;
	
	public List<double[]> getOnehour() {
		return onehour;
	}
	public void setOnehour(List<double[]> onehour) {
		this.onehour = onehour;
	}
	public List<double[]> getTwohours() {
		return twohours;
	}
	public void setTwohours(List<double[]> twohours) {
		this.twohours = twohours;
	}
	public List<double[]> getFourhours() {
		return fourhours;
	}
	public void setFourhours(List<double[]> fourhours) {
		this.fourhours = fourhours;
	}
	public List<double[]> getOneday() {
		return oneday;
	}
	public void setOneday(List<double[]> oneday) {
		this.oneday = oneday;
	}
}
