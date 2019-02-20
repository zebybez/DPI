package loanbroker;

import model.Bank;

import java.util.ArrayList;
import java.util.List;

public class CONST {

    private static final Bank ING = new Bank(0,100000, 10);
    private static final Bank ABN = new Bank(200000,300000, 20);
    private static final Bank RBO = new Bank(0,250000, 15);

    private static List<Bank> banks = new ArrayList<>();

    static{
        banks.add(ING);
        banks.add(ABN);
        banks.add(RBO);
    }

    public static List<Bank> getList(){
        return banks;
    }
}
