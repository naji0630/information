package com.crypto.component;

import com.crypto.client.coinmarket.CoinMarketCapListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse.MarketPair;
import com.crypto.dao.CoinMarketCapDao;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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

        List<UpbitDominance> dominances = new ArrayList<>();
        CoinMarketCapUpbitListingResponse response = coinMarketCapDao.getUpbitCoinList();
        final List<String> symbols = response.getData()
                .getMarketPairs()
                .stream()
                .map(MarketPair::getBaseCurrencySlug)
                .collect(Collectors.toList());

        System.out.println("Upbit Coin Total: " + symbols.size());

        for (String symbol : symbols) {

            CoinMarketCapListingResponse top100Market = coinMarketCapDao.getRankingList(symbol);
            List<MarketTradeVolume> markets = top100Market.getData().getMarketPairs().stream()
                    .map(x -> new MarketTradeVolume(x.getExchangeSlug(), x.getVolumeBase()))
                    .collect(Collectors.toList());

            MarketTrades temp = new MarketTrades(
                    markets.stream().map(MarketTradeVolume::getTradeVolume).reduce(BigDecimal.ZERO, BigDecimal::add),
                    symbol, markets);
            dominances.add(new UpbitDominance(temp.coinSymbol, temp.getUpbitDominance()));
        }

        dominances.stream()
                .sorted((x, y) -> y.getUpbitDominance().compareTo(x.getUpbitDominance()))
                .forEach(x -> System.out.println(x.getCoinSymbol() + "\t" + x.getUpbitDominance()));

    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class UpbitDominance {
        private String coinSymbol;
        private BigDecimal upbitDominance;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class MarketTradeVolume {
        private String marketSymbol;
        private BigDecimal tradeVolume;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class MarketTrades {
        private BigDecimal total;
        private String coinSymbol;
        private List<MarketTradeVolume> marketTradeVolumes;

        private BigDecimal getUpbitVolume() {
            return marketTradeVolumes.stream()
                    .filter(x -> x.getMarketSymbol().equals("upbit"))
                    .map(MarketTradeVolume::getTradeVolume)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal getUpbitDominance() {
            return getUpbitVolume().divide(total, 5, RoundingMode.HALF_UP);
        }
    }
}