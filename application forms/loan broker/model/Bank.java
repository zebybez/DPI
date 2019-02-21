package model;

public class Bank {
    private int minAmount;
    private int maxAmount;
    private int loanTime;
    private String bankName;

    public Bank(String bankName, int minAmount, int maxAmount, int loanTime) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.loanTime = loanTime;
        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(int loanTime) {
        this.loanTime = loanTime;
    }
}
