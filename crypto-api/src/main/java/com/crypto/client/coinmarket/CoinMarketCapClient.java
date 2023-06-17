package com.crypto.client.coinmarket;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "CoinMarketCapClient", url = "https://api.coinmarketcap.com")
public interface CoinMarketCapClient {

    @GetMapping(value = "/data-api/v3/cryptocurrency/market-pairs/latest?start=1&limit=5&category=spot&centerType=all&sort=cmc_rank_advanced", produces = "application/json", consumes = "application/json")
    CoinMarketCapListingResponse getCoinMarketDominanceInfo(@RequestParam(value = "slug") String slug);

    @GetMapping(value = "/data-api/v3/exchange/market-pairs/latest?slug=upbit&category=spot&limit=400", produces = "application/json", consumes = "application/json")
    CoinMarketCapUpbitListingResponse getUpbitCoinList();
}
//https://api.coinmarketcap.com/data-api/v3/cryptocurrency/market-pairs/latest?slug=bitcoin&start=1&limit=100&category=spot&centerType=all&sort=cmc_rank_advanced
//https://api.coinmarketcap.com/data-api/v3/cryptocurrency/market-pairs/latest?slug=bitcoin&start=1&limit=100&category=spot&centerType=all&sort=cmc_rank_advanced
//https://api.coinmarketcap.com/data-api/v3/cryptocurrency/listing?start=101&limit=100&sortBy=market_cap&sortType=desc&convert=USD,BTC,ETH&cryptoType=all&tagType=all&audited=false&aux=ath,atl,high24h,low24h,num_market_pairs,cmc_rank,date_added,max_supply,circulating_supply,total_supply,volume_7d,volume_30d,self_reported_circulating_supply,self_reported_market_cap
