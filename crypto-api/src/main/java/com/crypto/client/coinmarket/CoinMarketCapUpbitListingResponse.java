package com.crypto.client.coinmarket;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoinMarketCapUpbitListingResponse {
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
        private List<MarketPair> marketPairs;
    }

    @Setter
    @Getter
    public static class MarketPair {
        String StringexchangeId;
        String exchangeName;
        String baseCurrencySlug;
    }
}
