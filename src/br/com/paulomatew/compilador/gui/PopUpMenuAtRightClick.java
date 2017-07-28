package br.com.paulomatew.compilador.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class PopUpMenuAtRightClick extends JPopupMenu {

    //private JMenuItem anItem;
    private JTextComponent component;

    public PopUpMenuAtRightClick(JTextComponent component, JMenuItem[] menu) {
        this.component = component;

        for (JMenuItem in : menu) {
            if (in != null) {
                add(in);
            } else {
                add(new JPopupMenu.Separator());
            }

        }
    }
}
