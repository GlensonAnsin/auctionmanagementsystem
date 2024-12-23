package gui;

import models.Bid;
import models.Item;
import utils.FileHandler;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class BidDialog extends JDialog {
    private final Item item;
    private final String bidder;
    private JTextField bidAmountField;
    private static final String BIDS_FILE = "bids.dat";
    private static final String ITEMS_FILE = "items.dat";

    public BidDialog(Frame parent, Item item, String bidder) {
        super(parent, "Place Bid - " + item.getName(), true);
        this.item = item;
        this.bidder = bidder;

        setupUI();
    }

    private void setupUI() {
        setSize(400, 400); // Increased height to accommodate new fields
        setLocationRelativeTo(getParent());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Item details
        panel.add(new JLabel("Item Name: " + item.getName()), gbc);

        // Add description in a scroll pane
        JTextArea descriptionArea = new JTextArea(item.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(panel.getBackground());
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(0, 80));
        descScrollPane.setBorder(BorderFactory.createTitledBorder("Description"));
        panel.add(descScrollPane, gbc);

        panel.add(new JLabel("Current Price: ₱" + String.format("%.2f", item.getCurrentPrice())), gbc);
        panel.add(new JLabel("Seller: " + item.getSeller()), gbc);

        // Add status
        String status = item.getWinningBidder() != null ? "Highest Bidder: " + item.getWinningBidder() : "No bids yet";
        panel.add(new JLabel("Status: " + status), gbc);

        panel.add(new JLabel("End Time: " + item.getEndTime()), gbc);

        // Bid amount field
        bidAmountField = new JTextField(20);
        bidAmountField.setBorder(BorderFactory.createTitledBorder("Your Bid Amount (₱)"));
        panel.add(bidAmountField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton bidButton = new JButton("Place Bid");
        JButton cancelButton = new JButton("Cancel");

        bidButton.addActionListener(e -> placeBid());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(bidButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void placeBid() {
        try {
            double bidAmount = Double.parseDouble(bidAmountField.getText().trim());

            // Refresh item data to get latest price
            @SuppressWarnings("unchecked")
            ArrayList<Item> items = (ArrayList<Item>) FileHandler.readFromFile(ITEMS_FILE);
            Item currentItem = null;
            for (Item i : items) {
                if (i.getItemId() == item.getItemId()) {
                    currentItem = i;
                    break;
                }
            }

            if (currentItem == null || !currentItem.isActive()) {
                JOptionPane.showMessageDialog(this,
                        "This auction is no longer available!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            if (bidAmount <= currentItem.getCurrentPrice()) {
                JOptionPane.showMessageDialog(this,
                        "Bid must be higher than current price: ₱" + currentItem.getCurrentPrice(),
                        "Invalid Bid",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if auction has ended
            if (currentItem.getEndTime().before(new Date())) {
                JOptionPane.showMessageDialog(this,
                        "This auction has ended!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            // Create new bid with synchronized block to prevent race conditions
            synchronized (BidDialog.class) {
                @SuppressWarnings("unchecked")
                ArrayList<Bid> bids = (ArrayList<Bid>) FileHandler.readFromFile(BIDS_FILE);
                if (bids == null)
                    bids = new ArrayList<>();

                int newBidId = bids.size() + 1;
                Bid newBid = new Bid(newBidId, currentItem.getItemId(), bidder, bidAmount);
                bids.add(newBid);
                FileHandler.saveToFile(BIDS_FILE, bids);

                // Update item's current price and winning bidder
                currentItem.setCurrentPrice(bidAmount);
                currentItem.setWinningBidder(bidder); // Set winning bidder
                FileHandler.saveToFile(ITEMS_FILE, items);
            }

            JOptionPane.showMessageDialog(this,
                    "Bid placed successfully!\nYou are now the highest bidder.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid amount!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
