package com.crypto.component;

import com.crypto.client.coinmarket.CoinMarketCapListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse;
import com.crypto.client.coinmarket.CoinMarketCapUpbitListingResponse.MarketPair;
import com.crypto.dao.CoinMarketCapDao;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
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

    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 0)
    public void getUpbitCoinList() throws IOException, InterruptedException {

        Map<String, UpbitDominance> dominances = new HashMap<>();
        CoinMarketCapUpbitListingResponse response = coinMarketCapDao.getUpbitCoinList();
        final List<String> symbols = response.getData()
                .getMarketPairs()
                .stream()
                .map(MarketPair::getBaseCurrencySlug)
                .collect(Collectors.toList());

        for (String symbol : symbols) {

            CoinMarketCapListingResponse top100Market = coinMarketCapDao.getRankingList(symbol);
            List<MarketTradeVolume> markets = top100Market.getData().getMarketPairs().stream()
                    .map(x -> new MarketTradeVolume(x.getExchangeSlug(), x.getVolumeBase(), x.getQuote(), x.getQuoteSymbol()))
                    .collect(Collectors.toList());

            MarketTrades temp = new MarketTrades(
                    markets.stream().map(MarketTradeVolume::getTradeVolume).reduce(BigDecimal.ZERO, BigDecimal::add),
                    symbol, markets);
            dominances.put(temp.coinSymbol, new UpbitDominance(temp.coinSymbol, temp.getUpbitVolume(), temp.getUpbitDominance(),
                    temp.getUpbitPrice()));
        }

        System.out.println("Upbit Dominance");

        LocalDateTime now = LocalDateTime.now();
        String filename = "/Users/jihoon.na/IdeaProjects/information/crypto-api/src/main/resources/"+now+".tsv";
        File file = new File(filename);
        FileWriter fw = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fw);

        dominances.keySet().forEach(
                x -> {
                    try {
                        writer.write(
                                now + "\t" + dominances.get(x).getPrice() + "\t" + dominances.get(x).getCoinSymbol() + "\t" + dominances.get(x).getUpbitDominance()+"\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        writer.close();

    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class UpbitDominance {
        private String coinSymbol;
        private BigDecimal volume;
        private BigDecimal upbitDominance;
        private BigDecimal price;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public class MarketTradeVolume {
        private String marketSymbol;
        private BigDecimal tradeVolume;
        private BigDecimal price;
        private String quoteSymbol;
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

        private BigDecimal getUpbitPrice() {
            return marketTradeVolumes.stream()
                    .filter(x -> x.getMarketSymbol().equals("upbit"))
                    .filter(x->"KRW".equals(x.getQuoteSymbol()))
                    .map(MarketTradeVolume::getPrice)
                    .findAny().orElse(BigDecimal.ZERO);
        }

        private BigDecimal getUpbitDominance() {
            return getUpbitVolume().divide(total, 5, RoundingMode.HALF_UP);
        }
    }
}