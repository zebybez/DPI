package model.bank;

import java.io.Serializable;

/**
 * This class stores information about the bank reply
 * to a loan request of the specific client
 */
public class BankInterestReply implements Serializable {

    private double interest; // the loan interest
    private String bankId; // the nunique quote Id
    private int ssn;

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public BankInterestReply() {
        this.interest = 0;
        this.bankId = "";
    }

    public BankInterestReply(double interest, String quoteId, int ssn) {
        this.interest = interest;
        this.bankId = quoteId;
        this.ssn = ssn;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getQuoteId() {
        return bankId;
    }

    public void setQuoteId(String quoteId) {
        this.bankId = quoteId;
    }

    public String toString() {
        return "quote=" + this.bankId + " interest=" + this.interest;
    }
}
