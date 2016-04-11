package com.eurexchange.clear.tradegui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        initComponents();
    }

    private void initComponents() {

        JTabbedPane mainTabbedWindow = new JTabbedPane();
        JProgressBar progressBar = new JProgressBar();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainTabbedWindow.addTab("Trading", new TradingGuiTab(progressBar).invoke());
        mainTabbedWindow.addTab("Clearing ", new ClearingGuiTab().invoke());
        mainTabbedWindow.addTab("Trade Status", new TradeStatusGuiTab().invoke());
        mainTabbedWindow.addTab("Trade Confirmation", new TradeConfirmationGuiTab().invoke());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(mainTabbedWindow)
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(progressBar, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(105, 105, 105))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(mainTabbedWindow, GroupLayout.PREFERRED_SIZE, 472,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        mainTabbedWindow.getAccessibleContext().setAccessibleName("Trading");
        mainTabbedWindow.getAccessibleContext().setAccessibleDescription("");

        pack();
    }

    public static void main(String args[]) {

        try {
            UIManager.put("ProgressBar.background", Color.ORANGE);
            UIManager.put("ProgressBar.foreground", Color.BLUE);
            UIManager.put("ProgressBar.selectionBackground", Color.RED);
            UIManager.put("ProgressBar.selectionForeground", Color.GREEN);
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }

}
