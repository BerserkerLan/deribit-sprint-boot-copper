package com.copper.deribitclient.deribit;

import com.copper.deribitclient.Constants;
import com.copper.deribitclient.models.AccountSummary;
import com.copper.deribitclient.models.CurrencyInfo;
import com.copper.deribitclient.models.WalletInfo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class Client {

    Gson gson = new Gson();

    public String getWithdrawalHistory() {
        return gson.toJson(getWithdrawalsOfUser());
    }

    public String getDepositHistory() {
        return gson.toJson(getDepositsOfUser());
    }

    public String getUserBalanceAndReservedFunds() {
        return gson.toJson(getAccountSummaryOfUser());
    }

    public String withdrawCryptoToWallet(String currency, String address, double amount) {
        String rawWithdrawalResponse = getContentFromUrl(Constants.BASE_URL + Constants.WITHDRAW_CRYPTO_ENDPOINT + "address=" + address + "&amount=" + amount + "&currency=" + currency, true);
        if (rawWithdrawalResponse.equals("")) {
            return "Error occured, Please check your parameters and try again";
        }
        JsonElement error = JsonParser.parseString(rawWithdrawalResponse).getAsJsonObject().get("error");

        if (error == null) {
            return "Order Placed";
        }
        else {
            return "There was an error executing the order, reason: " + error.getAsJsonObject().get("data").getAsJsonObject().get("reason");
        }
    }

    private List<AccountSummary> getAccountSummaryOfUser() {
        ArrayList<AccountSummary> accountSummaryList = new ArrayList<>();
        CurrencyInfo[] currencyInfoList = getCurrencyNames();

        for (CurrencyInfo currencyInfo : currencyInfoList) {
            String rawAccountSummaryData = getContentFromUrl(Constants.BASE_URL + Constants.GET_ACCOUNT_SUMMARY_ENDPOINT + currencyInfo.getCurrency(), true);
            accountSummaryList.add(gson.fromJson(getInnerResultJsonFromContent(rawAccountSummaryData), AccountSummary.class));
        }
        return accountSummaryList;
    }

    private List<WalletInfo> getWithdrawalsOfUser() {
        ArrayList<WalletInfo> walletInfoList = new ArrayList<>();

        for (CurrencyInfo currencyInfo : getCurrencyNames()) {
            String rawWithdrawalData = getContentFromUrl(Constants.BASE_URL + Constants.GET_WITHDRAWALS_ENDPOINT + currencyInfo.getCurrency(), true);
            walletInfoList.add(gson.fromJson(getInnerResultJsonFromContent(rawWithdrawalData), WalletInfo.class));
        }
        return walletInfoList.stream().filter( walletInfo -> walletInfo.getAddress() != null).collect(Collectors.toList());
    }

    private List<WalletInfo> getDepositsOfUser() {
        ArrayList<WalletInfo> walletInfoList = new ArrayList<>();

        for (CurrencyInfo currencyInfo : getCurrencyNames()) {
            String rawDepositsData = getContentFromUrl(Constants.BASE_URL + Constants.GET_DEPOSITS_ENDPOINT + currencyInfo.getCurrency(), true);
            walletInfoList.add(gson.fromJson(getInnerResultJsonFromContent(rawDepositsData), WalletInfo.class));
        }
        return walletInfoList.stream().filter( walletInfo -> walletInfo.getAddress() != null).collect(Collectors.toList());
    }

    private CurrencyInfo[] getCurrencyNames() {
        String rawCurrencyData = getContentFromUrl(Constants.BASE_URL + Constants.GET_CURRENCY_ENDPOINT, false);
        CurrencyInfo[] currencyInfoList = gson.fromJson(getInnerResultJsonFromContent(rawCurrencyData), CurrencyInfo[].class);
        return currencyInfoList;

    }

    private String getContentFromUrl(String urlString, boolean isAuthRequired) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            if (isAuthRequired) {
                con.setRequestProperty("Authorization", getAuthorizationParam());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return content.toString();
        } catch (IOException e) {
            System.out.println("There was an error in your input, and thus the command failed");
        }
        return content.toString();
    }

    private String getAuthorizationParam() {
        String clientId = System.getenv().get("CLIENT_ID");
        String clientSecret = System.getenv().get("CLIENT_SECRET");
        return "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }

    private String getInnerResultJsonFromContent(String jsonStringWithResult) {
        JsonObject object = JsonParser.parseString(jsonStringWithResult).getAsJsonObject();
        return object.get("result").toString();
    }
}

