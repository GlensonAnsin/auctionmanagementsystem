package gui;

import models.Item;
import models.User;
import utils.ColorScheme;
import utils.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class MainDashboard extends JFrame {
    private User currentUser;
    private JTable auctionTable;
    private DefaultTableModel tableModel;
    private static final String ITEMS_FILE = "items.dat";

    public MainDashboard(User user) {
        this.currentUser = user;
        setTitle("Auction Dashboard - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Initialize components
        createAuctionTable();

        // Set up main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Set menu bar
        setJMenuBar(createMenuBar());

        // Add main panel to the frame
        add(mainPanel);

        // Final setup
        refreshTable();
        setVisible(true);

        // Add auto-refresh timer
        Timer refreshTimer = new Timer(30000, e -> refreshTable());
        refreshTimer.start();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Auction Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        JButton addItemButton = new JButton("Add New Item");
        JButton refreshButton = new JButton("Refresh");

        // Style both buttons consistently
        addItemButton.setBackground(ColorScheme.BUTTON_DEFAULT);
        addItemButton.setForeground(ColorScheme.BUTTON_TEXT);
        styleButton(addItemButton, "Add New Item", "plus-icon.png");

        refreshButton.setBackground(ColorScheme.BUTTON_DEFAULT);
        refreshButton.setForeground(ColorScheme.BUTTON_TEXT);
        styleButton(refreshButton, "Refresh", "refresh-icon.png");

        buttonPanel.add(addItemButton);
        buttonPanel.add(refreshButton);

        addItemButton.addActionListener(e -> openAddItemDialog());
        refreshButton.addActionListener(e -> {
            refreshTable();
            refreshButton.setBackground(ColorScheme.BUTTON_DEFAULT);
        });

        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table scroll pane with styling
        JScrollPane tableScrollPane = new JScrollPane(auctionTable);
        tableScrollPane.setPreferredSize(new Dimension(900, 500));
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(ColorScheme.BORDER)));

        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem profileItem = new JMenuItem("Profile");

        // Add logout action listener
        logoutItem.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        menu.add(profileItem);
        menu.add(logoutItem);
        menuBar.add(menu);
        return menuBar;
    }

    public void createAuctionTable() {
        String[] columns = { "ID", "Name", "Description", "Current Price", "End Time", "Seller", "Status", "Payment" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        auctionTable = new JTable(tableModel);

        // Set column widths
        auctionTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        auctionTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        auctionTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        auctionTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        auctionTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        auctionTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        auctionTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        auctionTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Apply your existing table styling
        auctionTable.setRowHeight(40);
        auctionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        auctionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        auctionTable.setFillsViewportHeight(true);
        auctionTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Add your existing table styling
        auctionTable.setBackground(ColorScheme.BACKGROUND);
        auctionTable.setForeground(ColorScheme.TABLE_TEXT);
        auctionTable.setGridColor(ColorScheme.SECONDARY);
        auctionTable.setSelectionBackground(ColorScheme.TABLE_SELECTION);
        auctionTable.setSelectionForeground(ColorScheme.TEXT_PRIMARY);

        // Table header styling
        auctionTable.getTableHeader().setBackground(ColorScheme.TABLE_HEADER);
        auctionTable.getTableHeader().setForeground(ColorScheme.TABLE_HEADER_TEXT);
        auctionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Add double-click listener
        auctionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = auctionTable.getSelectedRow();
                    if (row != -1) {
                        openBidDialog(row);
                    }
                }
            }
        });

        // Alternate row colors
        auctionTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ColorScheme.TABLE_EVEN_ROW : ColorScheme.TABLE_ODD_ROW);
                    c.setForeground(ColorScheme.TABLE_TEXT);
                }
                return c;
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        @SuppressWarnings("unchecked")
        ArrayList<Item> items = (ArrayList<Item>) FileHandler.readFromFile(ITEMS_FILE);
        Date now = new Date();

        if (items != null) {
            for (Item item : items) {
                Vector<Object> row = new Vector<>();
                row.add(item.getItemId());
                row.add(item.getName());
                row.add(item.getDescription());
                row.add(String.format("₱%.2f", item.getCurrentPrice()));
                row.add(item.getEndTime());
                row.add(item.getSeller());

                // Status column
                String status;
                if (item.isPaid()) {
                    status = "Sold to " + item.getWinningBidder();
                } else if (item.getEndTime().before(now)) {
                    if (item.getWinningBidder() != null) {
                        status = "Won by " + item.getWinningBidder();
                    } else {
                        status = "No Bids";
                    }
                } else {
                    if (item.getWinningBidder() != null) {
                        status = "Highest Bidder: " + item.getWinningBidder();
                    } else {
                        status = "Active";
                    }
                }
                row.add(status);

                // Payment column
                String paymentStatus;
                if (item.isPaid()) {
                    paymentStatus = "Paid";
                } else if (item.getEndTime().before(now) &&
                        item.getWinningBidder() != null &&
                        item.getWinningBidder().equals(currentUser.getUsername())) {
                    paymentStatus = "Payment Required";
                } else {
                    paymentStatus = "-";
                }
                row.add(paymentStatus);

                tableModel.addRow(row);
            }
        }
    }

    private void openAddItemDialog() {
        AddItemDialog dialog = new AddItemDialog(this, currentUser.getUsername());
        dialog.setVisible(true);
        refreshTable(); // Refresh the table after adding new item
    }

    private void openBidDialog(int row) {
        int itemId = (int) tableModel.getValueAt(row, 0);
        @SuppressWarnings("unchecked")
        ArrayList<Item> items = (ArrayList<Item>) FileHandler.readFromFile(ITEMS_FILE);

        if (items != null) {
            for (Item item : items) {
                if (item.getItemId() == itemId) {
                    // Payment handling for auction winner
                    if (item.getEndTime().before(new Date()) &&
                            item.getWinningBidder() != null &&
                            item.getWinningBidder().equals(currentUser.getUsername()) &&
                            !item.isPaid()) {

                        new PaymentDialog(this, item, currentUser.getUsername()).setVisible(true);
                        refreshTable();
                        return;
                    }

                    // Normal bidding process
                    if (!item.getEndTime().before(new Date())) {
                        if (!item.getSeller().equals(currentUser.getUsername())) {
                            new BidDialog(this, item, currentUser.getUsername()).setVisible(true);
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "You cannot bid on your own items!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "This auction has ended!",
                                "Cannot Bid",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    return;
                }
            }
        }
    }

    private void styleButton(JButton button, String text, String iconPath) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setIconTextGap(8);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ColorScheme.BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ColorScheme.BUTTON_DEFAULT);
            }
        });
    }
}
