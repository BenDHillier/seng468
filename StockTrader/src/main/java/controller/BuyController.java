package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BuyController {
    @PostMapping(path="/buy")
    public @ResponseBody String createNewBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount) {

        if (amount <= 0) {
            // TODO: send fail response
        }
        // get stock quote


    }
}
