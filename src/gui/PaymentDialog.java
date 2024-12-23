package gui;

import models.Item;
import utils.PaymentHandler;
import utils.FileHandler;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class PaymentDialog extends JDialog {
    private final Item item;
    private final String buyer;
    private static final String ITEMS_FILE = "items.dat";

    public PaymentDialog(Frame parent, Item item, String buyer) {
        super(parent, "Payment Required", true);
        this.item = item;
        this.buyer = buyer;

        setupUI();
    }

    private void setupUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Payment details
        panel.add(new JLabel("Item: " + item.getName()), gbc);
        panel.add(new JLabel("Final Price: ₱" + String.format("%.2f", item.getCurrentPrice())), gbc);
        panel.add(new JLabel("Seller: " + item.getSeller()), gbc);

        // Payment button
        JButton payButton = new JButton("Process Payment");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        payButton.addActionListener(e -> processPayment());
        cancelButton.addActionListener(e -> dispose());

        add(panel);
    }

    private void processPayment() {
        boolean success = PaymentHandler.processPayment(
                buyer,
                item.getSeller(),
                item.getCurrentPrice(),
                item.getName());

        if (success) {
            // Update item status
            @SuppressWarnings("unchecked")
            ArrayList<Item> items = (ArrayList<Item>) FileHandler.readFromFile(ITEMS_FILE);
            if (items != null) {
                for (Item i : items) {
                    if (i.getItemId() == item.getItemId()) {
                        i.setPaid(true);
                        break;
                    }
                }
                FileHandler.saveToFile(ITEMS_FILE, items);
            }
            dispose();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && (!item.getWinningBidder().equals(buyer) || item.isPaid())) {
            JOptionPane.showMessageDialog(this,
                    "Payment is not required or has already been processed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        super.setVisible(visible);
    }
}
