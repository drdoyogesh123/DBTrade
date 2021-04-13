package com.db;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TradeMsgStore {
	final protected Map<String, TradeMsg> store;
	protected long  lastProcessesdVersion;
	protected LocalDate today;
	protected long storeRefreshRate; //refresh rate is millis
	protected boolean updateDate = true;
	
	public TradeMsgStore() {
		store = new ConcurrentHashMap<String, TradeMsg>();
		lastProcessesdVersion =0;
		today = LocalDate.now();
		Instant instant = today.atStartOfDay(ZoneId.systemDefault()).toInstant();	
		long timeInMillis = instant.toEpochMilli();
		long currTimeMilli = System.currentTimeMillis();
		storeRefreshRate = (currTimeMilli - timeInMillis) + 24*60*60*1000;
		scheduleUpdateMaturityTask();
		System.out.println("Trade store initialized and task to update maturity scheduled");
	}	
	
	public void processTradeMsg(TradeMsg trade) throws Exception{
		if(trade.maturityDate.isBefore(today)){
			System.out.println("Maturity date can not be earlier than today " + trade);
			return;
		}
		String tradeId = trade.tradeId;
		if(trade.version > lastProcessesdVersion || store.containsKey(tradeId)){
			store.put(tradeId, trade);
			if(trade.version > lastProcessesdVersion){
				lastProcessesdVersion = trade.version;
			}
		}else{
			throw new Exception(tradeId + ", Invalid version number - either it does not exists in store or lower than expected " + trade.version );
		}
	}
	
	protected void scheduleUpdateMaturityTask(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Running update maturity task for : " + store.size() );
				store.values().stream().filter(v -> v.expired == false && v.maturityDate.isBefore(today))
						.forEach(t -> t.expired = true);
				if(updateDate){
					today = LocalDate.now();
				}
			}
		}, 0, storeRefreshRate);
	}
}		