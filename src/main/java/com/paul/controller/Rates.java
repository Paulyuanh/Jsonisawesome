package com.paul.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class Rates {

	@PostMapping("/getRates")
	public ResponseEntity getSearchResultViaAjax(@RequestParam("curr") String curr) throws Exception {

		String jsonStr = getRatesJson();
		JsonNode arrayNode = new ObjectMapper().readTree(jsonStr);
		List<Object> dataList = new ArrayList<>();
		
		if (curr != null || !"undefined".equals(curr)) {
			for (int i = 0; i < arrayNode.size(); i++) {
				switch (curr) {
				case "USD":
					dataList.add(arrayNode.get(i).get("Date").asText());
					dataList.add(arrayNode.get(i).get("USD/NTD").asText());
					continue;
				case "RMB":
					dataList.add(arrayNode.get(i).get("Date").asText());
					dataList.add(arrayNode.get(i).get("RMB/NTD").asText());
					continue;
				case "EUR":
					double eurRate = arrayNode.get(i).get("EUR/USD").asDouble() * arrayNode.get(i).get("USD/NTD").asDouble();
					dataList.add(arrayNode.get(i).get("Date").asText());
					dataList.add(String.format("%.3f", eurRate));
					continue;
				case "JPY":
					double ntdRate = 1/ (arrayNode.get(i).get("USD/NTD").asDouble());
					double jpyRate = ntdRate * (arrayNode.get(i).get("USD/JPY").asDouble());
					dataList.add(arrayNode.get(i).get("Date").asText());
					dataList.add(String.format("%.3f", jpyRate));
					continue;
				case "GBP":
					double gbpRate = arrayNode.get(i).get("GBP/USD").asDouble() * arrayNode.get(i).get("USD/NTD").asDouble();
					dataList.add(arrayNode.get(i).get("Date").asText());
					dataList.add(String.format("%.3f", gbpRate));
					continue;
				}
			}
		}

		return ResponseEntity.ok(dataList);
	}

	private static String getRatesJson() {
		String requestUrl = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";
		// buffer用於接受返回的字元
		StringBuffer buffer = new StringBuffer();
		try {
			// 建立URL,把請求地址給補全,其中urlencode()方法用於把params裡的引數給取出來
			URL url = new URL(requestUrl);
			// 開啟http連線
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.connect();

			// 獲得輸入
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			// 將bufferReader的值給放到buffer裡
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			// 關閉bufferReader和輸入流
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			// 斷開連線
			httpUrlConn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回字串
		return buffer.toString();
	}

}
