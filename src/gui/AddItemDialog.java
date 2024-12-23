package gui;

import models.Item;
import utils.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddItemDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField startingPriceField;
    private JSpinner durationSpinner;
    private JComboBox<String> timeUnitCombo;
    private final String seller;
    private static final String ITEMS_FILE = "items.dat";

    public AddItemDialog(Frame parent, String seller) {
        super(parent, "Add New Item", true);
        this.seller = seller;
        setSize(400, 450);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Item name
        nameField = new JTextField(20);
        nameField.setBorder(BorderFactory.createTitledBorder("Item Name"));
        panel.add(nameField, gbc);

        // Description
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Description"));
        panel.add(scrollPane, gbc);

        // Starting price
        startingPriceField = new JTextField(20);
        startingPriceField.setBorder(BorderFactory.createTitledBorder("Starting Price (₱)"));
        panel.add(startingPriceField, gbc);

        // Duration panel with spinner and time unit selector
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationPanel.setBorder(BorderFactory.createTitledBorder("Duration"));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 999, 1);
        durationSpinner = new JSpinner(spinnerModel);

        timeUnitCombo = new JComboBox<>(new String[] { "Minutes", "Hours", "Days" });

        durationPanel.add(durationSpinner);
        durationPanel.add(timeUnitCombo);
        panel.add(durationPanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Item");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> addItem());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void addItem() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = startingPriceField.getText().trim();
        int duration = (Integer) durationSpinner.getValue();
        String timeUnit = (String) timeUnitCombo.getSelectedItem();

        // Validation
        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double startingPrice;
        try {
            startingPrice = Double.parseDouble(priceText);
            if (startingPrice <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate end time based on selected time unit
        Calendar calendar = Calendar.getInstance();
        switch (timeUnit) {
            case "Minutes":
                calendar.add(Calendar.MINUTE, duration);
                break;
            case "Hours":
                calendar.add(Calendar.HOUR, duration);
                break;
            case "Days":
                calendar.add(Calendar.DAY_OF_MONTH, duration);
                break;
        }
        Date endTime = calendar.getTime();

        // Confirm if duration is very short
        if (timeUnit.equals("Minutes") && duration < 5) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Warning: Auction duration is very short (" + duration + " minutes).\nDo you want to continue?",
                    "Confirm Duration",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Generate new item ID and save
        @SuppressWarnings("unchecked")
        ArrayList<Item> items = (ArrayList<Item>) FileHandler.readFromFile(ITEMS_FILE);
        int newId = 1;
        if (items != null) {
            newId = items.stream().mapToInt(Item::getItemId).max().orElse(0) + 1;
        } else {
            items = new ArrayList<>();
        }

        Item newItem = new Item(newId, name, description, startingPrice, endTime, seller);
        items.add(newItem);
        FileHandler.saveToFile(ITEMS_FILE, items);

        JOptionPane.showMessageDialog(this,
                String.format("Item added successfully!\nAuction ends: %s", endTime.toString()),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
