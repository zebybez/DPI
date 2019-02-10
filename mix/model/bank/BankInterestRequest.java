package model.bank;

import java.io.Serializable;

/**
 *
 * This class stores all information about an request from a bank to offer
 * a loan to a specific client.
 */
public class BankInterestRequest implements Serializable {

    private int amount; // the requested loan amount
    private int time; // the requested loan period
    private int ssn;

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public BankInterestRequest() {
        super();
        this.ssn = 0;
        this.amount = 0;
        this.time = 0;
    }

    public BankInterestRequest(int amount, int time, int ssn) {
        super();
        this.amount = amount;
        this.time = time;
        this.ssn = ssn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return " amount=" + amount + " time=" + time + " ssn=" + ssn;
    }
}
