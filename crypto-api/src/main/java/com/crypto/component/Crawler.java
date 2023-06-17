package com.crypto.component;

import com.crypto.client.coinmarket.CoinMarketCapListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse.MarketPair;
import com.crypto.dao.CoinMarketCapDao;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Crawler {

    private final CoinMarketCapDao coinMarketCapDao;

    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 0)
    public void getUpbitCoinList() throws IOException, InterruptedException {
        CoinMarketCapUpbitListingResponse response = coinMarketCapDao.getUpbitCoinList();
        final List<String> symbols = response.getData()
                .getMarketPairs()
                .stream()
                .map(MarketPair::getBaseCurrencySlug)
                .collect(Collectors.toList());

        System.out.println("Upbit Coin Total: " + symbols.size());

        for (String symbol : symbols) {
            System.out.println("Symbol : " + symbol);

            CoinMarketCapListingResponse temp = coinMarketCapDao.getRankingList(symbol);
            List<String> markets = temp.getData().getMarketPairs().stream().map(x -> x.getExchangeName())
                    .collect(Collectors.toList());

            markets.forEach(x -> System.out.print(x+" | "));
            System.out.println();
            if (markets.contains("Upbit")) {
                System.out.println("This is Kimchi Coin!");
            }
            System.out.println("--------------------");
            Thread.sleep(1000);
        }
    }
}