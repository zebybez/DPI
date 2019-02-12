package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import messaging.service.Destinations;
import messaging.service.MessageService;
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
    private Map<Integer, LoanRequest> loanRequestMap;

    private MessageService msgServiceClientToBank;
    private MessageService msgServiceBankToClient;

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
        msgServiceClientToBank = new MessageService(Destinations.BANK_INTEREST_REQUEST, Destinations.LOAN_REQUEST, new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                System.out.println("loanBroker:received message from loanClient: " + msg);
                parseLoanRequest(msg);
            }
        });
        msgServiceBankToClient = new MessageService(Destinations.LOAN_REQUEST_REPLY, Destinations.BANK_INTEREST_REPLY, new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                System.out.println("loanBroker:received message from bank: " + msg);
                parseBankInterestReply(msg);
            }
        });
        loanRequestMap = new HashMap<>();
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

    private void parseLoanRequest(Message msg) {
        //todo create bankinterestrequest and send it to the bank
        System.out.println("we've arrived chief");
        ObjectMessage objMsg = (ObjectMessage) msg;
        try {
            LoanRequest loanRequest = (LoanRequest) objMsg.getObject();
            add(loanRequest);
            BankInterestRequest interestRequest = new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime(), loanRequest.getSsn());
            add(loanRequest, interestRequest);
            msgServiceClientToBank.sendMessage(interestRequest);
        } catch (JMSException | ClassCastException e) {
            e.printStackTrace();
        }

    }

    private void parseBankInterestReply(Message msg) {
        //todo create loanreply and send it to loanclient
        System.out.println("they've returned, chief");
        ObjectMessage objMsg = (ObjectMessage) msg;
        try {
            BankInterestReply reply = (BankInterestReply) objMsg.getObject();
            add(loanRequestMap.get(reply.getSsn()), reply);
            LoanReply loanReply = new LoanReply(reply.getInterest(), reply.getQuoteId(), reply.getSsn());
            msgServiceBankToClient.sendMessage(loanReply);
        } catch (JMSException e) {
            e.printStackTrace();
        }
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
        loanRequestMap.put(loanRequest.getSsn(), loanRequest);
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
            ;
            list.repaint();
        }
    }


}
