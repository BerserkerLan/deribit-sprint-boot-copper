package com.copper.deribitclient.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

public class AccountSummary {
    private int balance;
    private String currency;
    @SerializedName("available_withdrawal_funds")
    private int availableWithdrawalFunds;
    @SerializedName("available_funds")
    private int availableFunds;
    @JsonIgnore
    private int reservedFunds = availableFunds - availableWithdrawalFunds;
}
