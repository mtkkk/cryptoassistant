package com.cryptoassistant.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cryptoassistant.dao.SlackDAO;
import com.cryptoassistant.models.analysis.Analysis;
import com.cryptoassistant.models.cryptowatch.CandleStick;
import com.cryptoassistant.models.cryptowatch.CandleStickHolder;
import com.cryptoassistant.models.cryptowatch.OHLCResponse;
import com.cryptoassistant.models.slack.Answer;
import com.cryptoassistant.models.slack.Question;

public class AnalysisService {

	private static SlackDAO slackDAO = new SlackDAO();
	
	public Analysis makeAnalysis(String date, int days) {
		
		try {
			// Calculate all the indicators
			Analysis analysis = new Analysis();
			getCurrentPriceForDate("1 Day", analysis, date, days);
			calculateBollingerBandsForDate("1 Day", analysis, date);
			calculateRSIForDate("1 Day", analysis, date);
			calculateMACDForDate("1 Day", analysis, date);
			
			analyzeData(analysis);
			
			return analysis;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Analysis makeAnalysis() {
		
		try {
			Map<String, Long> closeTimes = slackDAO.getLastClosetime();
			CryptowatchApiService cwApi = new CryptowatchApiService();
			OHLCResponse candleData = new OHLCResponse();
			
			if(closeTimes.isEmpty()) {
				// Table is empty, get historical data
				candleData = cwApi.getHistoryData();
			} else {
				// Get data from last closetime onwards
				candleData = cwApi.getDataFromDateOnwards(closeTimes.get("1 Hour"));
			}
			
			
			CandleStickHolder candleHolder = new CandleStickHolder();
			candleHolder.setOnehour(parseCandles(candleData.getResult().getOnehour()));
			candleHolder.setTwohour(parseCandles(candleData.getResult().getTwohours()));
			candleHolder.setFourhour(parseCandles(candleData.getResult().getFourhours()));
			candleHolder.setOneDay(parseCandles(candleData.getResult().getOneday()));
			
			// Updates the first elements on the database and removes it from the candleHolder so it cant be saved again
			saveFirstElementAndRemove(candleHolder, closeTimes);
			
			slackDAO.saveCandles(candleHolder);
			
			// Calculate all the indicators
			Analysis analysis = new Analysis();
			getCurrentPrice("1 Day", analysis);
			calculateBollingerBands("1 Day", analysis);
			calculateRSI("1 Day", analysis);
			calculateMACD("1 Day", analysis);
			
			analyzeData(analysis);
			
			return analysis;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static void getCurrentPrice(String period, Analysis analysis) {
		
		double currentPrice = slackDAO.getCurrentPrice(period);
		
		analysis.setCurrentPrice(currentPrice);
	}
	
	private static void getCurrentPriceForDate(String period, Analysis analysis, String date, int days) {
		
		double currentPrice = slackDAO.getCurrentPriceForDate(period, date);
		
		analysis.setCurrentPrice(currentPrice);
	}

	public static List<CandleStick> parseCandles(List<double[]> candles) {
		
		List<CandleStick> candleList = new ArrayList<CandleStick>();
		
		try {
			for(double[] candle : candles) {
				CandleStick candleStick = new CandleStick(candle);
				candleList.add(candleStick);
			}
			
			return candleList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void saveFirstElementAndRemove(CandleStickHolder candleHolder, Map<String, Long> closeTimes) {
			
		CandleStick firstElement = new CandleStick();
		
		// 1 Hour
		firstElement = candleHolder.getOnehour().get(0);
		if(firstElement.getCloseTime() == closeTimes.get("1 Hour")) {
			slackDAO.updateCandle(firstElement, "1 Hour");
			candleHolder.getOnehour().remove(0);
		}
		
		// 2 Hour
		firstElement = candleHolder.getTwohour().get(0);
		if(firstElement.getCloseTime() == closeTimes.get("2 Hours")) {
			slackDAO.updateCandle(firstElement, "2 Hours");
			candleHolder.getTwohour().remove(0);
		}
		
		//4 Hour
		firstElement = candleHolder.getFourhour().get(0);
		if(firstElement.getCloseTime() == closeTimes.get("4 Hours")) {
			slackDAO.updateCandle(firstElement, "4 Hours");
			candleHolder.getFourhour().remove(0);
		}
		
		// 1 Day
		firstElement = candleHolder.getOneDay().get(0);
		if(firstElement.getCloseTime() == closeTimes.get("1 Day")) {
			slackDAO.updateCandle(firstElement, "1 Day");
			candleHolder.getOneDay().remove(0);
		}
	}
	
	public static void calculateBollingerBands(String period, Analysis analysis) {
		
		List<CandleStick> twentyDaysCandles = slackDAO.getXdaysHistory(period, 20);
		
		analysis.setSma(calculateSMA(period, twentyDaysCandles));
		analysis.setStandardDeviation(calculateSD(twentyDaysCandles));
		analysis.setUpperBollingerBand(calculateUpperBollingerBand(analysis.getSma(), analysis.getStandardDeviation()));
		analysis.setLowerBollingerBand(calculateLowerBollingerBand(analysis.getSma(), analysis.getStandardDeviation()));
	}
	
	public static void calculateBollingerBandsForDate(String period, Analysis analysis, String date) {
		
		List<CandleStick> twentyDaysCandles = slackDAO.getXdaysHistoryForDate(period, 20, date);
		
		analysis.setSma(calculateSMA(period, twentyDaysCandles));
		analysis.setStandardDeviation(calculateSD(twentyDaysCandles));
		analysis.setUpperBollingerBand(calculateUpperBollingerBand(analysis.getSma(), analysis.getStandardDeviation()));
		analysis.setLowerBollingerBand(calculateLowerBollingerBand(analysis.getSma(), analysis.getStandardDeviation()));
	}
	
	public static double calculateSMA(String period, List<CandleStick> twentyDaysCandles) {
		
		double sum = 0;
		
		for(CandleStick candle : twentyDaysCandles) {
			sum += candle.getClosePrice();
		}
		
		double average = sum / twentyDaysCandles.size();
		
		return average;
	}
	
	public static double calculateSD(List<CandleStick> twentyDaysCandles) {
		
		double[] numArray = new double[twentyDaysCandles.size()];
		
		for(int i = 0 ; i < twentyDaysCandles.size() ; i++) {
			numArray[i] = twentyDaysCandles.get(i).getClosePrice();
		}
		
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;
        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation/length-1);
    }
	
	public static double calculateUpperBollingerBand(double sma, double sd) {
		
		double upperBand = sma + (sd * 2);
		
		return upperBand;
	}
	
	public static double calculateLowerBollingerBand(double sma, double sd) {
		
		double lowerBand = sma - (sd * 2);
		
		return lowerBand;
	}
	
	public static void calculateRSI(String period, Analysis analysis) {
		
		int n = 31;
		List<CandleStick> candles = slackDAO.getXdaysHistory(period, n);
		n = candles.size();
		double[] RSIs = new double[n];
		double[] avgGains = new double[n];
		double[] avgLosses = new double[n];
		double gainSum = 0;
		double lossSum = 0;
		double avgGain = 0;
		double avgLoss = 0;
		
		for(int i = 0 ; i < candles.size() ; i++) {
			
			if(i > 0 && i <= 14) {
				double changeValue = candles.get(i).getClosePrice() - candles.get(i-1).getClosePrice();
				
				if(changeValue > 0) {
					gainSum += changeValue;
				} else {
					lossSum += Math.abs(changeValue);
				}
				
				if(i == 14) {
					avgGain = gainSum / 14;
					avgLoss = lossSum / 14;
					double firstRS =  avgGain / avgLoss;
					double rsi = 100 - (100 / (1+firstRS));
					RSIs[i] = rsi;
					avgGains[i] = avgGain;
					avgLosses[i] = avgLoss;
				}
			} else if(i > 14) {
				// Smoothing RS 14
				double changeValue = candles.get(i).getClosePrice() - candles.get(i-1).getClosePrice();
				double gain = 0;
				double loss = 0;
				if(changeValue > 0) {
					gain = changeValue;
				} else {
					loss = Math.abs(changeValue);
				}
				
				avgGain = ((avgGains[i-1] * 13) + gain)/14;
				avgLoss = ((avgLosses[i-1] * 13) + loss)/14;
				
				double firstRS = avgGain / avgLoss;
				double rsi = 100 - (100 / (1+firstRS));
				RSIs[i] = rsi;
				avgGains[i] = avgGain;
				avgLosses[i] = avgLoss;
			}
		}
		
		analysis.setRsi(RSIs[n-1]);
	}
	
	public static void calculateRSIForDate(String period, Analysis analysis, String date) {
		
		int n = 31;
		List<CandleStick> candles = slackDAO.getXdaysHistoryForDate(period, n, date);
		n = candles.size();
		double[] RSIs = new double[n];
		double[] avgGains = new double[n];
		double[] avgLosses = new double[n];
		double gainSum = 0;
		double lossSum = 0;
		double avgGain = 0;
		double avgLoss = 0;
		
		for(int i = 0 ; i < candles.size() ; i++) {
			
			if(i > 0 && i <= 14) {
				double changeValue = candles.get(i).getClosePrice() - candles.get(i-1).getClosePrice();
				
				if(changeValue > 0) {
					gainSum += changeValue;
				} else {
					lossSum += Math.abs(changeValue);
				}
				
				if(i == 14) {
					avgGain = gainSum / 14;
					avgLoss = lossSum / 14;
					double firstRS =  avgGain / avgLoss;
					double rsi = 100 - (100 / (1+firstRS));
					RSIs[i] = rsi;
					avgGains[i] = avgGain;
					avgLosses[i] = avgLoss;
				}
			} else if(i > 14) {
				// Smoothing RS 14
				double changeValue = candles.get(i).getClosePrice() - candles.get(i-1).getClosePrice();
				double gain = 0;
				double loss = 0;
				if(changeValue > 0) {
					gain = changeValue;
				} else {
					loss = Math.abs(changeValue);
				}
				
				avgGain = ((avgGains[i-1] * 13) + gain)/14;
				avgLoss = ((avgLosses[i-1] * 13) + loss)/14;
				
				double firstRS = avgGain / avgLoss;
				double rsi = 100 - (100 / (1+firstRS));
				RSIs[i] = rsi;
				avgGains[i] = avgGain;
				avgLosses[i] = avgLoss;
			}
		}
		
		analysis.setRsi(RSIs[n-1]);
	}
	
	public void calculateMACD(String period, Analysis analysis) {
		
		
		int n = 94;
		List<CandleStick> candles = slackDAO.getXdaysHistory(period, n);
		n = candles.size();
		double sum = 0;
		double twelveSMA = 0;
		double twentysixSMA = 0;
		double nineSMA = 0;
		double macdSUM = 0;
		double[] twelveDayEMA = new double[n];
		double[] twentysixDayEMA = new double[n];
		double[] macd = new double[n];
		double[] nineDayEMA = new double [n];
		
		for(int i = 0 ; i < n ; i++) {
			
			if(i < 11) {
				sum += candles.get(i).getClosePrice();
			} else if(i == 11) {
				sum += candles.get(i).getClosePrice();
				twelveSMA = sum / 12;
				// The first 12-Day EMA is calculated by the Simple Moving Average of the first 12 days
				twelveDayEMA[i] = twelveSMA;
			} else {
				sum += candles.get(i).getClosePrice();
				double dayPrice = candles.get(i).getClosePrice();
				// The subsequent 12-Day EMA are calculated by
				// (The most recent price - previous day value) * the multiplier + previous day value
				twelveDayEMA[i] = ((dayPrice - twelveDayEMA[i-1]) * analysis.getTwelveDayEMAMult()) + twelveDayEMA[i-1];
				
				if(i == 25) {
					twentysixSMA = sum / 26;
					// The first 26-Day EMA is calculated by the Simple Moving Average of the first 26 days
					twentysixDayEMA[i] = twentysixSMA;
					macd[i] = twelveDayEMA[i] - twentysixDayEMA[i];
					macdSUM += macd[i];
				} else if(i > 25) {
					// The subsequent 26-Day EMA are calculated by
					// (The most recent price - previous day 26-Day EMA value) * the multiplier + previous day  26-Day EMA value
					twentysixDayEMA[i] = ((dayPrice - twentysixDayEMA[i-1]) * analysis.getTwentysixDayEMAMult()) + twentysixDayEMA[i-1];
					
					macd[i] = twelveDayEMA[i] - twentysixDayEMA[i];
					macdSUM += macd[i];
					
					if(i == 33) {
						// The first 9-Day EMA is calculated by the Simple Moving Average of the first 9 MACDs
						nineSMA = macdSUM / 9;
						nineDayEMA[i] = nineSMA;
					} else if(i > 33) {
						// The subsequent 9-Day EMA are calculated by
						// (The most recent MACD - previous 9-Day EMA value) * the multiplier + previous day 9-Day EMA value
						nineDayEMA[i] = ((macd[i] - nineDayEMA[i-1]) * analysis.getNineDayEMAMult()) + nineDayEMA[i-1];
					}
				}
			
				if(i == n-1) {
					analysis.setMacd(macd[i]);
					analysis.setNineDayEMA(nineDayEMA[i]);
					analysis.setMacdHistogramValue(analysis.getMacd() - analysis.getNineDayEMA());
				}
			}
		}
		
	}
	
	public void calculateMACDForDate(String period, Analysis analysis, String date) {
		
		
		int n = 94;
		List<CandleStick> candles = slackDAO.getXdaysHistoryForDate(period, n, date);
		n = candles.size();
		double sum = 0;
		double twelveSMA = 0;
		double twentysixSMA = 0;
		double nineSMA = 0;
		double macdSUM = 0;
		double[] twelveDayEMA = new double[n];
		double[] twentysixDayEMA = new double[n];
		double[] macd = new double[n];
		double[] nineDayEMA = new double [n];
		
		for(int i = 0 ; i < n ; i++) {
			
			if(i < 11) {
				sum += candles.get(i).getClosePrice();
			} else if(i == 11) {
				sum += candles.get(i).getClosePrice();
				twelveSMA = sum / 12;
				// The first 12-Day EMA is calculated by the Simple Moving Average of the first 12 days
				twelveDayEMA[i] = twelveSMA;
			} else {
				sum += candles.get(i).getClosePrice();
				double dayPrice = candles.get(i).getClosePrice();
				// The subsequent 12-Day EMA are calculated by
				// (The most recent price - previous day value) * the multiplier + previous day value
				twelveDayEMA[i] = ((dayPrice - twelveDayEMA[i-1]) * analysis.getTwelveDayEMAMult()) + twelveDayEMA[i-1];
				
				if(i == 25) {
					twentysixSMA = sum / 26;
					// The first 26-Day EMA is calculated by the Simple Moving Average of the first 26 days
					twentysixDayEMA[i] = twentysixSMA;
					macd[i] = twelveDayEMA[i] - twentysixDayEMA[i];
					macdSUM += macd[i];
				} else if(i > 25) {
					// The subsequent 26-Day EMA are calculated by
					// (The most recent price - previous day 26-Day EMA value) * the multiplier + previous day  26-Day EMA value
					twentysixDayEMA[i] = ((dayPrice - twentysixDayEMA[i-1]) * analysis.getTwentysixDayEMAMult()) + twentysixDayEMA[i-1];
					
					macd[i] = twelveDayEMA[i] - twentysixDayEMA[i];
					macdSUM += macd[i];
					
					if(i == 33) {
						// The first 9-Day EMA is calculated by the Simple Moving Average of the first 9 MACDs
						nineSMA = macdSUM / 9;
						nineDayEMA[i] = nineSMA;
					} else if(i > 33) {
						// The subsequent 9-Day EMA are calculated by
						// (The most recent MACD - previous 9-Day EMA value) * the multiplier + previous day 9-Day EMA value
						nineDayEMA[i] = ((macd[i] - nineDayEMA[i-1]) * analysis.getNineDayEMAMult()) + nineDayEMA[i-1];
					}
				}
			
				if(i == n-1) {
					analysis.setMacd(macd[i]);
					analysis.setNineDayEMA(nineDayEMA[i]);
					analysis.setMacdHistogramValue(analysis.getMacd() - analysis.getNineDayEMA());
				}
			}
		}
		
	}
	
	public void analyzeData(Analysis analysis) {
		
		double finalDecision = 0;
		
		/*
		 * Weight
		 * Bollinger Bands 	- 20%
		 * RSI				- 40%
		 * MACD Histogram	- 40%
		 * Price Action		- 0%
		 */
		
		//Bollinger Bands
		double bbandsDecision = analyze1DayBollingerBands(analysis);
		
		//RSI
		double RSI = analyze1DayRSI(analysis);
		
		//MACD
		double macd = analyze1DayMACD(analysis);
		
		finalDecision = (bbandsDecision*0.2) + (RSI*0.4) +(macd*0.4);
		
		
		if(finalDecision >= 0) {
			
			if(finalDecision >= 0.9) {
				analysis.setDecisionText("90% certain to sell");
				analysis.setIsWorthy(true);
			} else if(finalDecision >= 0.85) {
				analysis.setDecisionText("85% certain to sell");
				analysis.setIsWorthy(true);
			} else if(finalDecision >= 0.8) {
				analysis.setDecisionText("80% certain to sell");
				analysis.setIsWorthy(true);
			} else if(finalDecision >= 0.75) {
				analysis.setDecisionText("75% certain to sell");
				analysis.setIsWorthy(true);
			} else if(finalDecision >= 0.7) {
				analysis.setDecisionText("70% certain to sell");
				analysis.setIsWorthy(true);
			} else if(finalDecision >= 0.6) {
				analysis.setDecisionText("60% certain to sell");
			} else if(finalDecision >= 0.5) {
				analysis.setDecisionText("50% certain to sell");
			} else {
				analysis.setDecisionText("Inconclusive");
			}
		} else {
			
			if(finalDecision <= -0.9) {
				analysis.setDecisionText("90% certain to buy");
				analysis.setIsWorthy(true);
			} else if(finalDecision <= -0.8) {
				analysis.setDecisionText("80% certain to buy");
				analysis.setIsWorthy(true);
			} else if(finalDecision <= -0.7) {
				analysis.setDecisionText("70% certain to buy");
				analysis.setIsWorthy(true);
			} else if(finalDecision <= -0.6) {
				analysis.setDecisionText("60% certain to buy");
			} else if(finalDecision <= -0.5) {
				analysis.setDecisionText("50% certain to buy");
			} else {
				analysis.setDecisionText("Inconclusive");
			}
		}
		
	}
	
	public double analyze1DayBollingerBands(Analysis analysis) {
		
		double decisionFactor = 0;
		double currentPrice = analysis.getCurrentPrice();
		
		if(currentPrice > analysis.getSma()) {
			// The price is in the upper half of the bollinger bands
			double upperBandCoefficient = analysis.getCurrentPrice() / analysis.getUpperBollingerBand();
			
			if(upperBandCoefficient >= 1.095) {
				// Extremely overbought
				decisionFactor = 0.95;
			} else if(upperBandCoefficient >= 1.05) {
				// Strongly overbought
				decisionFactor = 0.8;
			} else if(upperBandCoefficient >= 1) {
				// Overbought
				decisionFactor = 0.6;
			} else if(upperBandCoefficient >= 0.95) {
				// Almost overbought
				decisionFactor = 0.5;
			}
		} else {
			// The price is in the lower half of the bollinger bands
			double lowerBandCoefficient = analysis.getCurrentPrice() / analysis.getLowerBollingerBand();
			
			if(lowerBandCoefficient <= 0.85) {
				// Extremely oversold
				decisionFactor = -0.95;
			} else if(lowerBandCoefficient <= 0.9) {
				// Strongly oversold
				decisionFactor = -0.8;
			} else if(lowerBandCoefficient <= 0.95) {
				// Oversold
				decisionFactor = -0.6;
			} else if(lowerBandCoefficient <= 1) {
				// Almost oversold
				decisionFactor = -0.5;
			}
		}
		
		return decisionFactor;
	}

	public double analyze1DayRSI(Analysis analysis) {
		
		double decisionFactor = 0;
		double rsi = analysis.getRsi();
		
		if(rsi >= 60) {
			//Generally overbought
			
			if(rsi >= 85) {
				// Extremely overbought
				decisionFactor = 0.95;
			} else if(rsi >= 80) {
				// Strongly overbought
				decisionFactor = 0.8;
			} else if(rsi >= 75) {
				// Overbought
				decisionFactor = 0.6;
			} else {
				// Almost overbought
				decisionFactor = 0.5;
			}
			
		} else if( rsi <= 40) {
			// Generally oversold
			
			if(rsi <= 20) {
				// Extremely oversold
				decisionFactor = -0.95;
			} else if(rsi <= 30) {
				// Strongly oversold
				decisionFactor = -0.8;
			} else if(rsi <= 35) {
				// Oversold
				decisionFactor = -0.6;
			} else {
				// Almost oversold
				decisionFactor = -0.5;
			}
		}
		
		return decisionFactor;
	}
	
	public double analyze1DayMACD(Analysis analysis) {
		
		double decisionFactor = 0;
		double histogramValue = analysis.getMacdHistogramValue();
		
		if(histogramValue >= 0) {
			// Generally indicates price is going up
			
			if(histogramValue >= 400) {
				// Greatest bull run ever
				decisionFactor = 1.1;
			} else if(histogramValue >= 200) {
				// Great bull run
				decisionFactor = 0.95;
			} else if(histogramValue >= 150) {
				decisionFactor = 0.8;
			} else if(histogramValue >= 100) {
				decisionFactor = 0.6;
			} else if(histogramValue >= 90) {
				decisionFactor = 0.5;
			}
		} else {
			// Generally indicates price is going down
			
			if(histogramValue <= -400) {
				// Big crash or correction
				decisionFactor = -1.1;
			} else if(histogramValue <= -200) {
				decisionFactor = -0.95;
			} else if(histogramValue <= -170) {
				decisionFactor = -0.8;
			} else if(histogramValue <= -150) {
				decisionFactor = -0.6;
			}
		}
		
		
		return decisionFactor;
	}
	
	public Answer buildAnswersWithAnalysis(Analysis analysis, String token, String channel) {
		
		StringBuilder strBuilder = new StringBuilder();
		Answer ans = new Answer();
		ans.setToken(token);
		ans.setChannel(channel);
		
		// Header
		strBuilder.append("===============================================================");
		strBuilder.append("\n                                                         Start of Analysis");
		strBuilder.append("\n===============================================================\n");
		
		// Bollinger Bands
		strBuilder.append("------------ Bollinger Bands ------------");
		strBuilder.append("\n Upper Band: " + analysis.getUpperBollingerBand());
		strBuilder.append("\n Price: " + analysis.getCurrentPrice());
		strBuilder.append("\n Lower Band: " + analysis.getLowerBollingerBand());
		strBuilder.append("\n-------------------------------------------");
		
		// RSI
		strBuilder.append("\n RSI: " + analysis.getRsi());
		
		// MACD
		strBuilder.append("\n----------------- MACD -----------------");
		strBuilder.append("\n MACD: " + analysis.getMacd());
		strBuilder.append("\n Signal: " + analysis.getNineDayEMA());
		strBuilder.append("\n Histogram: " + analysis.getMacdHistogramValue());
		strBuilder.append("\n-------------------------------------------\n");
		
		// Final decision
		strBuilder.append("\n Decision: " + analysis.getDecisionText());
		
		// Footer
		strBuilder.append("\n===============================================================");
		strBuilder.append("\n                                                          End of Analysis");
		strBuilder.append("\n===============================================================\n");
		
		ans.setText(strBuilder.toString());
		
		return ans;
	}
}
