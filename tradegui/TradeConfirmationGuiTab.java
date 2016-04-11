package com.eurexchange.clear.tradegui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TradeConfirmationGuiTab {
    private JPanel tradeConfirmationPanel;
    private JScrollPane jScrollPane7;
    private JTable jTable6;

    public TradeConfirmationGuiTab(){
        tradeConfirmationPanel = new JPanel();
        jScrollPane7 = new JScrollPane();
        jTable6 = new JTable();

    }
    public JPanel invoke(){
        jTable6.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "Member", "Message", "Title 3", "Title 4"
                }
        ) {

            Class[] types = new Class[] {
                    String.class, String.class, Object.class, Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable6);

        GroupLayout tradeConfirmationPanelLayout = new GroupLayout(tradeConfirmationPanel);
        tradeConfirmationPanel.setLayout(tradeConfirmationPanelLayout);
        tradeConfirmationPanelLayout.setHorizontalGroup(
                tradeConfirmationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(tradeConfirmationPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane7, GroupLayout.PREFERRED_SIZE, 588, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(696, Short.MAX_VALUE))
        );
        tradeConfirmationPanelLayout.setVerticalGroup(
                tradeConfirmationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, tradeConfirmationPanelLayout.createSequentialGroup()
                                .addGap(0, 128, Short.MAX_VALUE)
                                .addComponent(jScrollPane7, GroupLayout.PREFERRED_SIZE, 316, GroupLayout.PREFERRED_SIZE))
        );
        return tradeConfirmationPanel;
    }

}
