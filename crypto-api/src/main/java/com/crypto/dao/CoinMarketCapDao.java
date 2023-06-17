package com.crypto.dao;

import com.crypto.client.coinmarket.CoinMarketCapClient;
import com.crypto.client.coinmarket.CoinMarketCapListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinMarketCapDao {
    private final CoinMarketCapClient coinMarketCapClient;

    public CoinMarketCapUpbitListingResponse getUpbitCoinList(){
        return coinMarketCapClient.getUpbitCoinList();
    }

    public CoinMarketCapListingResponse getRankingList(String slug){
        return coinMarketCapClient.getCoinMarketDominanceInfo(slug);
    }
}