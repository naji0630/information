package com.crypto.client.coinmarket;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoinMarketCapListingResponse {
    private Status status;
    private Data data;

    @Setter
    @Getter
    public static class Status {
        private String timestamp;
        private int errorCode;
        private String errorMessage;
    }

    @Setter
    @Getter
    public static class Data {
        String id;
        String name;
        String symbol;
        String numMarketPairs;
        List<MarketPair> marketPairs;
    }

    @Setter
    @Getter
    public static class MarketPair {
        String exchangeId;
        String exchangeName;
        String exchangeSlug;
        String quoteSymbol;
        BigDecimal price;
        BigDecimal volumeBase;
        BigDecimal volumeUsd;
        BigDecimal volumeQuote;
    }
}