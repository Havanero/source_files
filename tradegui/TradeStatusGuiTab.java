package com.eurexchange.clear.tradegui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TradeStatusGuiTab {

    private JScrollPane jScrollPane6;
    private JTable jTable5;
    private JPanel statusPanel;

    public TradeStatusGuiTab() {
        statusPanel = new JPanel();
        jScrollPane6 = new JScrollPane();
        jTable5 = new JTable();

    }

    public JPanel invoke() {
        jTable5.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null },
                        { null, null },
                        { null, null },
                        { null, null }
                },
                new String[] {
                        "Process Status", "Count"
                }
        ) {

            Class[] types = new Class[] {
                    String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable5);

        GroupLayout statusPanelLayout = new GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane6, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 842, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                                .addGap(0, 249, Short.MAX_VALUE)
                                .addComponent(jScrollPane6, GroupLayout.PREFERRED_SIZE, 195,
                                        GroupLayout.PREFERRED_SIZE))
        );
        return statusPanel;
    }

}
