package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddController {

    private AccountRepository accountRepository;

    public AddController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PutMapping(value = "/add")
    public @ResponseBody
    HttpStatus addToAccountBalance(@RequestParam String userId,
                                   @RequestParam int amount) {
        try {
//            if( amount <= 0 ) {
//                throw new IllegalArgumentException(
//                        "The ADD amount parameter must be greater than zero");
//            }
//            else {
                accountRepository.updateAccountBalance(userId, amount);
//            }
            return HttpStatus.OK;
        } catch( IllegalArgumentException e ) {
            System.out.println("Exception in AddController: " + e.toString());
            return HttpStatus.BAD_REQUEST;
        }
    }
}