package messaging.service;

public enum Destinations {

    LOAN_REQUEST("queue.loanRequest"),
    BANK_INTEREST_REQUEST("queue.bankInterestRequest"),
    BANK_INTEREST_REPLY("queue.bankInterestReply"),
    LOAN_REQUEST_REPLY("queue.loanInterestReply");

    private String destination;

    Destinations(String destination){
        this.destination = destination;
    }

    @Override
    public String toString() {
        return destination;
    }
}
