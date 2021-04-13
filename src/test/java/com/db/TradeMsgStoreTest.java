package com.db;

import java.time.LocalDate;

import org.junit.Test;

import junit.framework.TestCase;

public class TradeMsgStoreTest extends TestCase{
	
	@Test
	public void testTradeWithMaturityDateAsToday(){
		TradeMsg trade1 = new TradeMsg("T1", "B1", "CP1", 1, LocalDate.now());
		TradeMsgStore ts = new TradeMsgStore();
		try {
			ts.processTradeMsg(trade1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(1, ts.store.size());
	}
	
	@Test
	public void testTradeWithMaturityDateAsYesterday(){
		TradeMsg trade1 = new TradeMsg("T1", "B1", "CP1", 1, LocalDate.now().minusDays(1));
		TradeMsgStore ts = new TradeMsgStore();
		try {
			ts.processTradeMsg(trade1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(0, ts.store.size()); // not processed
	}
	
	@Test
	public void testTradeWithMaturityDateAsTomorrow(){
		TradeMsg trade1 = new TradeMsg("T1", "B1", "CP1", 1, LocalDate.now().plusDays(1));
		TradeMsgStore ts = new TradeMsgStore();
		try {
			ts.processTradeMsg(trade1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(1, ts.store.size()); // processed
	}

	@Test(expected = Exception.class) // this can be speciallized
	public void testLowerVersionNumberIsRejected(){
		TradeMsg trade1 = new TradeMsg("T1", "B1", "CP1", 1, LocalDate.now());
		TradeMsg trade2 = new TradeMsg("T2", "B2", "CP2", 3, LocalDate.now());
		TradeMsg trade3 = new TradeMsg("T3", "B3", "CP3", 2, LocalDate.now()); //version lower than last processed
		
		TradeMsgStore ts = new TradeMsgStore();
		try {
			ts.processTradeMsg(trade1);
			ts.processTradeMsg(trade2);
			ts.processTradeMsg(trade3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(2, ts.store.size());
		assertFalse(ts.store.containsKey("T3") ); //T3 should not be on store
	}
	
	@Test
	public void testAutomaticUpdateOfMaturity(){
		TradeMsg trade1 = new TradeMsg("T1", "B1", "CP1", 1, LocalDate.now()); //Maturity date = Today
		TradeMsg trade2 = new TradeMsg("T2", "B2", "CP2", 2, LocalDate.now()); //Maturity date = Today
		TradeMsg trade3 = new TradeMsg("T3", "B3", "CP3", 3, LocalDate.now()); //Maturity date = Today
		TradeMsg trade4 = new TradeMsg("T4", "B4", "CP4", 4, LocalDate.now().plusDays(5)); //Maturity date = Today +5
		TradeMsg trade5 = new TradeMsg("T5", "B5", "CP5", 5, LocalDate.now().plusDays(5)); //Maturity date = Today +5
		TradeMsg trade6 = new TradeMsg("T6", "B6", "CP6", 6, LocalDate.now().plusDays(5)); //Maturity date = Today +5
		
		TradeMsgStore ts = new TradeMsgStore();
		ts.updateDate = false;
		ts.storeRefreshRate = 3000;
		try {
			ts.processTradeMsg(trade1);
			ts.processTradeMsg(trade2);
			ts.processTradeMsg(trade3);
			ts.processTradeMsg(trade4);
			ts.processTradeMsg(trade5);
			ts.processTradeMsg(trade6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(6, ts.store.size());
		for(TradeMsg t : ts.store.values()){
			assertFalse(t.expired);
		}	
		ts.today = LocalDate.now().plusDays(1); // temporarily set the today to tomorrow and wait for refresh rate
		try {
			Thread.sleep(ts.storeRefreshRate + 5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(6, ts.store.size());
		assertTrue(ts.store.get("T1").expired);  //Should be expired
		assertTrue(ts.store.get("T2").expired); //Should be expired
		assertTrue(ts.store.get("T3").expired); //Should be expired
		assertFalse(ts.store.get("T4").expired); //Should be not expired
		assertFalse(ts.store.get("T5").expired); //Should be not expired
		assertFalse(ts.store.get("T6").expired); //Should be not expired
	}
}
