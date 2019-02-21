package handler;

import loanbroker.Config;
import model.Bank;

import java.util.ArrayList;
import java.util.List;

public class LoanHandler{

    public List<String> check(int amount, int time) {

        List<String> acceptedBanks = new ArrayList<>();
        for (Bank bank : Config.getList()) {
            if (amount >= bank.getMinAmount() && amount <= bank.getMaxAmount() && time <= bank.getLoanTime()) {
                acceptedBanks.add(bank.getBankName());
            }
        }
        return acceptedBanks;
    }
}
