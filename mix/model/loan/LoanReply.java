package model.loan;

import java.io.Serializable;

/**
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
public class LoanReply implements Serializable {

    private double interest; // the interest that the bank offers
    private String bankID; // the unique quote identification
    private int ssn;

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public LoanReply() {
        super();
        this.interest = 0;
        this.bankID = "";
    }

    public LoanReply(double interest, String quoteID, int ssn) {
        super();
        this.interest = interest;
        this.bankID = quoteID;
        this.ssn = ssn;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getQuoteID() {
        return bankID;
    }

    public void setQuoteID(String quoteID) {
        this.bankID = quoteID;
    }

    @Override
    public String toString() {
        return " interest=" + String.valueOf(interest) + " quoteID=" + String.valueOf(bankID);
    }
}
