package bank;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import messaging.service.ApplicationGateway;
import messaging.service.Destinations;
import model.bank.*;
import messaging.requestreply.RequestReply;

public class JMSBankFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tfReply;
    private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>>();
    JList<RequestReply<BankInterestRequest, BankInterestReply>> list;
    private String quoteId;

    private ApplicationGateway<BankInterestRequest, BankInterestReply> appGateway;
    private Map<BankInterestRequest, String> replyCorrelationMap;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JMSBankFrame frame = new JMSBankFrame();
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
    public JMSBankFrame() {
        quoteId = "ABN AMRO";
        appGateway = new ApplicationGateway(Destinations.BANK_INTEREST_REPLY, Destinations.BANK_INTEREST_REQUEST){
            @Override
            public void parseMessage(Serializable object, String correlationId) {
                parseInterestRequest((BankInterestRequest) object, correlationId);
            }
        };
        replyCorrelationMap = new HashMap<>();
        setTitle("JMS Bank - ABN AMRO");
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
        gbc_scrollPane.gridwidth = 5;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        list = new JList<RequestReply<BankInterestRequest, BankInterestReply>>(listModel);
        scrollPane.setViewportView(list);

        JLabel lblNewLabel = new JLabel("type reply");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        contentPane.add(lblNewLabel, gbc_lblNewLabel);

        tfReply = new JTextField();
        GridBagConstraints gbc_tfReply = new GridBagConstraints();
        gbc_tfReply.gridwidth = 2;
        gbc_tfReply.insets = new Insets(0, 0, 0, 5);
        gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfReply.gridx = 1;
        gbc_tfReply.gridy = 1;
        contentPane.add(tfReply, gbc_tfReply);
        tfReply.setColumns(10);

        JButton btnSendReply = new JButton("send reply");
        btnSendReply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendInterestReply();
            }
        });
        GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
        gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnSendReply.gridx = 4;
        gbc_btnSendReply.gridy = 1;
        contentPane.add(btnSendReply, gbc_btnSendReply);
    }

    private void sendInterestReply(){
        RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
        double interest = Double.parseDouble((tfReply.getText()));
        BankInterestRequest request = rr.getRequest();
        BankInterestReply reply = new BankInterestReply(interest, quoteId, request.getSsn());
        if (rr != null && reply != null) {
            rr.setReply(reply);
            list.repaint();
            appGateway.createMessage(reply);
            appGateway.setCorrelationId(replyCorrelationMap.get(request));
            appGateway.sendMessage();
            //todo: sent JMS message with the reply to Loan Broker
        }
    }

    private void parseInterestRequest(BankInterestRequest request, String correlationId) {
        //todo this thing here you know what i mean.
//        ObjectMessage objMsg = (ObjectMessage) msg;
//        BankInterestRequest request = null;
//        try {
//            request = (BankInterestRequest) objMsg.getObject();
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
        RequestReply<BankInterestRequest, BankInterestReply> rr = new RequestReply<>(request, null);
        replyCorrelationMap.put(request, correlationId);
        listModel.addElement(rr);

    }

}
