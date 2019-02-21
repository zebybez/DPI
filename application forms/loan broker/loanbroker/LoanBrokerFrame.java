package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serializable;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import handler.LoanHandler;
import messaging.service.ApplicationGateway;
import messaging.service.Destinations;
import messaging.service.MessageGateway;
import model.bank.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;


public class LoanBrokerFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
    private JList<JListLine> list;

    private MessageGateway msgServiceClientToBank;
    private MessageGateway msgServiceBankToClient;
    private ApplicationGateway<LoanRequest, BankInterestRequest> clientToBankGateway;
    private ApplicationGateway<BankInterestReply, LoanReply> bankToClientGateway;
    private LoanHandler loanHandler;

    private Map<String, String> correlationMap;
    private Map<String, LoanRequest> requestMap;
    private Map<String, Integer> outToBankAmountMap;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoanBrokerFrame frame = new LoanBrokerFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Create the frame.
     */
    public LoanBrokerFrame() {
//        msgServiceClientToBank = new MessageGateway(Destinations.BANK_INTEREST_REQUEST, Destinations.LOAN_REQUEST, new MessageListener() {
//            @Override
//            public void onMessage(Message msg) {
//                parseLoanRequest(msg);
//            }
//        });
//        msgServiceBankToClient = new MessageGateway(Destinations.LOAN_REQUEST_REPLY, Destinations.BANK_INTEREST_REPLY, new MessageListener() {
//            @Override
//            public void onMessage(Message msg) {
//                System.out.println("loanBroker:received message from bank: " + msg);
//                parseBankInterestReply(msg);
//            }
//        });
        clientToBankGateway = new ApplicationGateway(Destinations.BANK_INTEREST_REQUEST.toString(), Destinations.LOAN_REQUEST.toString()){
            @Override
            public void parseMessage(Serializable object, String correlationId) {
                System.out.println("loanBroker:received message from loanClient");
                parseLoanRequest((LoanRequest) object, correlationId);
            }
        };
        bankToClientGateway = new ApplicationGateway(Destinations.LOAN_REQUEST_REPLY.toString(), Destinations.BANK_INTEREST_REPLY.toString()){
            @Override
            public void parseMessage(Serializable object, String correlationId) {
                System.out.println("loanBroker:received message from bank");
                parseBankInterestReply((BankInterestReply)object, correlationId);
            }
        };
        loanHandler = new LoanHandler();
        requestMap = new HashMap<>();
        correlationMap = new HashMap<>();
        outToBankAmountMap = new HashMap<>();
        setTitle("Loan Broker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
        gbl_contentPane.rowHeights = new int[]{233, 23, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 7;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        list = new JList<JListLine>(listModel);
        scrollPane.setViewportView(list);
    }

    private void parseLoanRequest(LoanRequest loanRequest, String correlationId) {
        add(loanRequest);
        BankInterestRequest interestRequest = new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime(), loanRequest.getSsn());
        add(loanRequest, interestRequest);
//        clientToBankGateway.createMessage(interestRequest);
//        requestMap.put(clientToBankGateway.getMessageId(), loanRequest);
//        clientToBankGateway.setCorrelationId(clientToBankGateway.getMessageId());
//        clientToBankGateway.sendMessage();

        String aggregationId = UUID.randomUUID().toString();
        requestMap.put(aggregationId, loanRequest);

        sendToMultipleBank(loanHandler.check(loanRequest.getAmount(), loanRequest.getTime()),interestRequest,aggregationId,correlationId);



    }

    private void sendToMultipleBank(List<String> bankList, BankInterestRequest interestRequest, String aggregationId, String correlationId){
        outToBankAmountMap.put(aggregationId, bankList.size());
        correlationMap.put(aggregationId, correlationId);
        for(String bank : bankList){
            interestRequest.setAggregationId(aggregationId);

            clientToBankGateway.createMessage(interestRequest);
            clientToBankGateway.setCorrelationId(correlationId);
            clientToBankGateway.setOutgoingQueue(Destinations.BANK_INTEREST_REQUEST+bank);
            clientToBankGateway.sendMessage();
        }

    }

    private void parseBankInterestReply(BankInterestReply reply, String correlationId) {

        add(requestMap.get(reply.getAggregrationId()), reply);
        LoanReply loanReply = new LoanReply(reply.getInterest(), reply.getQuoteId(), reply.getSsn());
        bankToClientGateway.createMessage(loanReply);
        bankToClientGateway.setCorrelationId(correlationMap.get(reply.getAggregrationId()));
        bankToClientGateway.sendMessage();
    }

    private JListLine getRequestReply(LoanRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getLoanRequest() == request) {
                return rr;
            }
        }

        return null;
    }

    public void add(LoanRequest loanRequest) {
        listModel.addElement(new JListLine(loanRequest));
    }


    public void add(LoanRequest loanRequest, BankInterestRequest bankRequest) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankRequest != null) {
            rr.setBankRequest(bankRequest);
            list.repaint();
        }
    }

    public void add(LoanRequest loanRequest, BankInterestReply bankReply) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankReply != null) {
            rr.setBankReply(bankReply);
            list.repaint();
        }
    }


}
