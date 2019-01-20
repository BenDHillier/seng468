package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.Investment;
import com.restResource.StockTrader.entity.InvestmentId;
import com.restResource.StockTrader.entity.PendingBuy;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.repository.InvestmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.service.QuoteService;

import java.time.LocalDateTime;


@RestController
@RequestMapping(value = "/buy")
public class BuyController {

    private QuoteService quoteService;

    private BuyRepository buyRepository;

    private InvestmentRepository investmentRepository;

    public BuyController(QuoteService quoteService, BuyRepository buyRepository, InvestmentRepository investmentRepository) {
        this.buyRepository = buyRepository;
        this.quoteService = quoteService;
        this.investmentRepository = investmentRepository;
    }

    @PostMapping(path = "/create")
    public @ResponseBody
    Quote createBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        // TODO: Confirm user has enough funds and set aside funds.

        Quote quote = quoteService.getQuote(stockSymbol, userId);

        if (quote.getPrice() > amount) {
            // TODO: may want to handle this differently.
            throw new IllegalArgumentException("The amount parameter must be greater than the quote price");
        }

        PendingBuy pendingBuy = PendingBuy.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .amount(amount)
                .timestamp(quote.getTimestamp())
                .price(quote.getPrice())
                .build();

        buyRepository.save(pendingBuy);

        return quote;
    }

    @PostMapping(path = "/commit")
    public @ResponseBody
    HttpStatus commitBuy(@RequestParam String userId) {
        PendingBuy pendingBuy = getMostRecentPendingBuy(userId);

        InvestmentId investmentId = InvestmentId.builder()
                .owner(userId)
                .stockSymbol(pendingBuy.getStockSymbol())
                .build();

        int amountToBuy = pendingBuy.getAmount() / pendingBuy.getPrice();

        Investment investment = investmentRepository
                .findById(investmentId)
                .orElse(Investment
                        .builder()
                        .investmentId(investmentId)
                        .amount(0)
                        .build());
        investment.setAmount(investment.getAmount() + amountToBuy);
        investmentRepository.save(investment);

        int remainingFundsFromBuy =
                pendingBuy.getAmount() - (pendingBuy.getPrice() * amountToBuy);
        //TODO: add remainingFundsFromBuy back to users account.

        return HttpStatus.OK;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    int cancelBuy(@RequestParam String userId) {
        return -1;
    }

    // TODO: change from exceptions to something else.
    // I think that it'd be best to return a failed http status code with a message.
    private PendingBuy getMostRecentPendingBuy(String userId) {
        while (true) {
            PendingBuy pendingBuy =
                    buyRepository
                            .findMostRecentForUserId(userId)
                            .orElseThrow(() -> new IllegalStateException(
                                    "There was nothing to commit."));

            if (pendingBuy
                    .getTimestamp()
                    .isBefore(LocalDateTime.now().minusMinutes(1))) {
                throw new IllegalStateException(
                        "The most recent buy has already expired.");
            }

            // If delete fails, then the pendingBuy has already been claimed and
            // we need to get the next most recent pendingBuy.
            try {
                buyRepository.deleteById(pendingBuy.getId());
            } catch (Exception e) {
                continue;
            }
            return pendingBuy;
        }
    }
}
