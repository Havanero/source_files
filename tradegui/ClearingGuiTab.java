package com.eurexchange.clear.tradegui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ClearingGuiTab {
    private JPanel clearingPanel;
    private JSeparator jSeparator3;
    private JLabel label1;
    private JScrollPane jScrollPane5;
    private JTable jTable4;


    public ClearingGuiTab(){
        clearingPanel = new JPanel();
        label1 = new JLabel();
        jSeparator3 = new JSeparator();
        jScrollPane5 = new JScrollPane();
        jTable4 = new JTable();

    }

    public JPanel invoke(){
        label1.setText("Welcome to Clearing Window");

        jTable4.setModel(new DefaultTableModel(
                new Object [][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane5.setViewportView(jTable4);



        label1.getAccessibleContext().setAccessibleName("Welcome to Clearing Window");

        GroupLayout clearingPanelLayout = new GroupLayout(clearingPanel);
        clearingPanel.setLayout(clearingPanelLayout);
        clearingPanelLayout.setHorizontalGroup(
                clearingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator3, GroupLayout.Alignment.TRAILING)
                        .addGroup(GroupLayout.Alignment.TRAILING, clearingPanelLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE)
                                .addGap(304, 304, 304))
                        .addGroup(clearingPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane5, GroupLayout.PREFERRED_SIZE, 1077, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 217, Short.MAX_VALUE))
        );
        clearingPanelLayout.setVerticalGroup(
                clearingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(clearingPanelLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator3, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane5, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE)
                                .addGap(69, 69, 69))
        );

        return clearingPanel;
    }

}
