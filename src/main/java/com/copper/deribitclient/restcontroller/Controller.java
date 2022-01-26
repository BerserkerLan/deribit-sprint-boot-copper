package com.copper.deribitclient.restcontroller;

import com.copper.deribitclient.deribit.Client;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class Controller {

    Client client = new Client();

    @GetMapping("/balance")
    public String getUserBalanceAndReservedFunds() {
        return client.getUserBalanceAndReservedFunds();
    }

    @GetMapping("/deposits")
    public String getUserDeposits() {
        return client.getDepositHistory();
    }

    @GetMapping("/withdrawals")
    public String getUserWithdrawals() {
        return client.getWithdrawalHistory();
    }

    @GetMapping("/withdraw")
    public String withdraw(@RequestParam String currency, @RequestParam String address, @RequestParam double amount) {
        return client.withdrawCryptoToWallet(currency, address, amount);
    }


}
