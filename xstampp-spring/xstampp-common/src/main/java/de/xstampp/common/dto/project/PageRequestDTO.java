package de.xstampp.common.dto.project;

public class PageRequestDTO {
	
	public static final int DEFAULT_FROM = 0;
	public final static int DEFAULT_AMOUNT = 100;
	
	Integer amount;
	Integer from;
	
	public PageRequestDTO() {
		/* public default constructor required for data mapping */
	}
	
	public Integer getAmount() {
		return amount != null ? amount : DEFAULT_AMOUNT;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getFrom() {
		return from != null ? from : DEFAULT_FROM;
	}
	public void setFrom(Integer from) {
		this.from = from;
	}
}
