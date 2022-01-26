package com.copper.deribitclient.models;

import java.math.BigDecimal;

public class WalletInfo {
    private String address;
    private BigDecimal amount;
    private String currency;

    public String getAddress() {
        return address;
    }
}
