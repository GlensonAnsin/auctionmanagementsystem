package utils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class PaymentHandler {
    private static final String DATA_DIR = "data";
    private static final String PAYMENTS_FILE = "payments.dat";

    public static boolean processPayment(String buyer, String seller, double amount, String itemName) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Simplified payment form with just mobile number and PIN
        JTextField mobileNumber = new JTextField(11);
        mobileNumber.setBorder(BorderFactory.createTitledBorder("GCash Mobile Number"));

        JPasswordField pin = new JPasswordField(6);
        pin.setBorder(BorderFactory.createTitledBorder("GCash PIN"));

        panel.add(new JLabel("Payment Amount: ₱" + String.format("%.2f", amount)), gbc);
        panel.add(mobileNumber, gbc);
        panel.add(pin, gbc);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "GCash Payment", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Validate payment details
            if (!validatePaymentDetails(mobileNumber.getText(), new String(pin.getPassword()))) {
                JOptionPane.showMessageDialog(null,
                        "Invalid GCash details!\nPlease check your mobile number and PIN.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Save payment record
            PaymentRecord payment = new PaymentRecord(buyer, seller, amount, itemName, new Date());
            savePaymentRecord(payment);

            JOptionPane.showMessageDialog(null,
                    "Payment processed successfully!\nAmount: ₱" + String.format("%.2f", amount),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    private static boolean validatePaymentDetails(String mobileNumber, String pin) {
        // Validate PH mobile number (must start with 09 and have 11 digits)
        if (!mobileNumber.matches("^(09)\\d{9}$")) {
            JOptionPane.showMessageDialog(null,
                    "Mobile number must start with '09' and be 11 digits long",
                    "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate MPIN (4-digit PIN for GCash)
        if (!pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(null,
                    "GCash MPIN must be 4 digits",
                    "Invalid PIN",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static void savePaymentRecord(PaymentRecord payment) {
        File file = new File(DATA_DIR, PAYMENTS_FILE);
        ArrayList<PaymentRecord> payments;

        if (file.exists()) {
            payments = (ArrayList<PaymentRecord>) FileHandler.readFromFile(PAYMENTS_FILE);
        } else {
            payments = new ArrayList<>();
        }

        payments.add(payment);
        FileHandler.saveToFile(PAYMENTS_FILE, payments);
    }
}

class PaymentRecord implements Serializable {
    private final String buyer;
    private final String seller;
    private final double amount;
    private final String itemName;
    private final Date paymentDate;
    private final String paymentId;

    public PaymentRecord(String buyer, String seller, double amount, String itemName, Date paymentDate) {
        this.buyer = buyer;
        this.seller = seller;
        this.amount = amount;
        this.itemName = itemName;
        this.paymentDate = paymentDate;
        this.paymentId = generatePaymentId();
    }

    private String generatePaymentId() {
        return "PAY-" + System.currentTimeMillis();
    }

    // Getters
    public String getBuyer() {
        return buyer;
    }

    public String getSeller() {
        return seller;
    }

    public double getAmount() {
        return amount;
    }

    public String getItemName() {
        return itemName;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
