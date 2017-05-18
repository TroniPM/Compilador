/*
 * Copyright 2016 Paulo Mateus [UFRPE-UAG] <paulomatew@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.paulomatew.compilador.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class PopUpMenuAtRightClickMainEditorListener extends MouseAdapter {

    private JTextComponent component;
    public ImageIcon iconCopy, iconPaste, iconCut, iconDelete, iconSelect;

    PopUpMenuAtRightClickMainEditorListener(JTextPane jTextPane1) {
        this.component = jTextPane1;

        iconCopy = new ImageIcon("./images/copy.png");
        iconPaste = new ImageIcon("./images/paste.png");
        iconCut = new ImageIcon("./images/cut.png");
        iconDelete = new ImageIcon("./images/delete.png");
        iconSelect = new ImageIcon("./images/select.png");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            component.requestFocus();
            doPop(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            component.requestFocus();
            doPop(e);
        }
    }

    public PopUpMenuAtRightClickMainEditorListener(JTextComponent component) {
        this.component = component;
    }

    private void doPop(MouseEvent e) {
        JMenuItem[] aux = new JMenuItem[]{
            new JMenuItemWithTag("Clear", iconDelete, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    component.setText("");
                }
            }),
            new JMenuItemWithTag("Copy", iconCopy, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String regex = component.getSelectedText();
                    if (regex == null) {
                        regex = "";
                    }
                    copyToClipboard(regex);
                }
            }),
            new JMenuItemWithTag("Paste", iconPaste, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String regex = component.getSelectedText();
                    String clipbString = pasteFromClipboard();
                    String allText = component.getText();
                    if (regex == null) {
                        regex = "";
                    }
                    if (regex.equals("")) {
                        int pos = component.getCaretPosition();
                        String newAllText = allText.substring(0, pos) + clipbString;
                        int newPos = newAllText.length();//Adicionar cursor após a inserção
                        newAllText += allText.substring(pos, allText.length());
                        component.setText(newAllText);
                        component.setCaretPosition(newPos);
                    } else {
                        component.replaceSelection(clipbString);
                    }
                }
            }),
            new JMenuItemWithTag("Cut", iconCut, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String txtRegex = component.getSelectedText();
                    if (txtRegex == null) {
                        txtRegex = "";
                    }
                    String allTxt = component.getText();
                    component.setText(allTxt.replace(txtRegex, ""));

                    copyToClipboard(txtRegex);
                }
            }),
            new JMenuItemWithTag("Select all", iconSelect, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    component.selectAll();
                }
            })};
        PopUpMenuAtRightClick menu = new PopUpMenuAtRightClick(component, aux);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    public class JMenuItemWithTag extends JMenuItem {

        public JMenuItemWithTag(String label, ActionListener action) {
            super(label);
            this.addActionListener(action);
        }

        public JMenuItemWithTag(String label, Icon icon, ActionListener action) {
            super(label, icon);
            this.addActionListener(action);
        }
    }

    public void copyToClipboard(String aTxt) {
        StringSelection stringSelection = new StringSelection(aTxt);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    public String pasteFromClipboard() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText
                = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }

        return (result);
    }
}
