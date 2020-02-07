package com.hxr.springrediskafka.entity.kafka;

public class TradeMsg {

    private int tradeId;
    private String cpty;
    private int quantity;
    private int price;

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public String getCpty() {
        return cpty;
    }

    public void setCpty(String cpty) {
        this.cpty = cpty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public TradeMsg(int tradeId, String cpty, int quantity, int price) {
        this.tradeId = tradeId;
        this.cpty = cpty;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return "TradeMsg [" +
                "tradeId=" + tradeId +
                ", cpty='" + cpty + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ']';
    }
}
