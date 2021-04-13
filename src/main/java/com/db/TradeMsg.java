package com.db;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class TradeMsg {
	String tradeId;
	long version;
	String counterPartyId;
	String bookId;
	LocalDate maturityDate;
	LocalDate creationDate;
	boolean expired = false;

	
	public TradeMsg() {
		creationDate = LocalDate.now();
	}
	public TradeMsg(String tradeId, String bookId, String counterpartyId, long version, LocalDate maturiryDate){
		this.tradeId = tradeId;
		this.bookId = bookId;
		this.version = version;
		this.counterPartyId=counterpartyId;
		this.maturityDate = maturiryDate;
		this.creationDate = LocalDate.now();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeMsg [tradeId=");
		builder.append(tradeId);
		builder.append(", version=");
		builder.append(version);
		builder.append(", counterPartyId=");
		builder.append(counterPartyId);
		builder.append(", bookId=");
		builder.append(bookId);
		builder.append(", maturityDate=");
		builder.append(maturityDate);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", expired=");
		builder.append(expired);
		builder.append("]");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		LocalDate today = LocalDate.now();
		ChronoUnit.SECONDS.between(today, today.plusDays(1));
	}
}

