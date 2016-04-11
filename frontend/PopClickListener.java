package com.eurexchange.clear.frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

public class PopClickListener extends MouseAdapter {

    JTable jTableComponent = null;
    DefaultTableModel model = null;
    JTextComponent jTextComponent = null;
    private Clipboard clipboard;

    public void mousePressed(MouseEvent e) {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (e.isPopupTrigger())
            doPop(e);
        if(e.getComponent() instanceof JTable)
            jTableComponent = (JTable) e.getSource();
        if(e.getComponent() instanceof JTextComponent)
            jTextComponent = (JTextComponent)e.getSource();

    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
        PopupMenu menu = new PopupMenu((JComponent) e.getComponent());
        if(e.getComponent() instanceof JTable){
            menu.show(jTableComponent, e.getX(), e.getY());
            model = (DefaultTableModel) jTableComponent.getModel();
            menu.deleteRow.addActionListener(e1 -> {
                model.removeRow(jTableComponent.getSelectedRow());
                model.fireTableDataChanged();
            });
            menu.copyRow.addActionListener(e1 -> {
                Object values = model.getDataVector().get(jTableComponent.getSelectedRow());
                Vector value = (Vector) values;
                jTableComponent.clearSelection();
                Enumeration enumeration = value.elements();
                Vector<Object> row = new Vector<>();

                while(enumeration.hasMoreElements()){
                    Object addedRows = enumeration.nextElement();
                    row.add(addedRows);
                }
                model.insertRow(jTableComponent.getRowCount(),row);
                model.fireTableDataChanged();
            });
        }
        if(e.getComponent() instanceof JTextComponent){
            menu.show(e.getComponent(), e.getX(), e.getY());
            menu.anItem.addActionListener(e1 -> jTextComponent.copy());

            menu.anItem1.addActionListener(e1 -> {
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    jTextComponent.paste();
                }
            });

            menu.clearItem.addActionListener(e1 -> jTextComponent.setText(""));


        }
    }
}
