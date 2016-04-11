package com.eurexchange.clear.frontend;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class PopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    JMenuItem anItem;
    JMenuItem anItem1;
    JMenuItem clearItem;
    JMenuItem copyRow;
    JMenuItem deleteRow;
    public PopupMenu(JComponent jComponent) {
        if (jComponent instanceof JTable){
            System.out.println("Showing only jtable menu popup");
            copyRow = new JMenuItem("Copy Entire Row");
            deleteRow = new JMenuItem("Delete Row");
            add(copyRow);
            add(deleteRow);
        }
        if (jComponent instanceof JTextComponent){
            anItem = new JMenuItem("Copy");
            anItem1 = new JMenuItem("Paste");
            clearItem = new JMenuItem("Clear Screen");

            add(anItem);
            add(anItem1);
            add(clearItem);
        }

    }
}
