package de.xstampp.common.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.xstampp.common.dto.mock.Filter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequestDTO {
	public static final int DEFAULT_FROM = 0;
	public final static int DEFAULT_AMOUNT = 100;
	public static final String DEFAULT_ORDER_BY = "id";
	public static final String DEFAULT_ORDER_DIRECTION = "asc";

	Filter filter;
	String orderBy;
	String orderDirection;
	Integer from;
	Integer amount;
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public String getOrderBy() {
		return orderBy == null ? DEFAULT_ORDER_BY : orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderDirection() {
		return orderDirection == null ? DEFAULT_ORDER_DIRECTION : orderDirection;
	}
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}

	public int getFrom() {
		return from == null ? DEFAULT_FROM : from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public int getAmount() {
		return amount == null ? DEFAULT_AMOUNT : amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}

