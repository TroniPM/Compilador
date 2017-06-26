/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.paulomatew.compilador.gui;

import static br.com.paulomatew.compilador.entities.OSValidator.isUnix;
import br.com.paulomatew.compilador.main.Compilador;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Mateus
 */
public class JFramePrincipal extends javax.swing.JFrame {

    /*x.x.x.x
    1º X = gramática
    2º X = minor changes
    4º X = gui
     */
    private String title = "Compilador (P.Mateus): 7.0.2";

    private DefaultStyledDocument doc;
    public javax.swing.JTextPane editor;

    public Compilador compiler = null;
    public ImageIcon iconExecute, iconList;

    private String consoleText = "";
    private Font fontBasica = null;

    /**
     * Creates new form JFramePrincipal
     */
    public JFramePrincipal() {
        if (!isUnix()) {
            //System.out.println("This is Windows|Mac");
            fontBasica = new java.awt.Font("Courier New", 0, 13);
        } else {
            fontBasica = new java.awt.Font("DejaVu Sans Mono", 0, 13);
            //System.out.println("This is Unix or Linux");
        }

        compiler = new Compilador();

        iconExecute = new ImageIcon("./images/execute.png");
        iconList = new ImageIcon("./images/list.png");

        initEditor();

        initComponents();
        setTitle(title);

        TextLineNumber tln = new TextLineNumber(jTextPane1);
        jScrollPane1.setRowHeaderView(tln);
        LinePainter lp = new LinePainter(jTextPane1, Color.decode("#eeeeee"));

        editor = jTextPane1;//Dou uma referência

        jTextPane1.addMouseListener(new PopUpMenuAtRightClickMainEditorListener(jTextPane1));

        setLocationRelativeTo(null);
    }

    private void initEditor() {

        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet attr_blue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
        //final AttributeSet attr_green = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.GREEN);
        final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

        doc = new DefaultStyledDocument() {
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                str = str.replaceAll("\t", "    ");//tab size
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offset);
                if (before < 0) {
                    before = 0;
                }
                int after = findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                String reserved = "";
                for (int i = 0; i < Compilador.RESERVED_WORDS.size(); i++) {
                    reserved += Compilador.RESERVED_WORDS.get(i);

                    if (i + 1 < Compilador.RESERVED_WORDS.size()) {
                        reserved += "|";
                    } else {
                        reserved += "|#";
                    }
                }

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches(
                                "(\\W)*("
                                + reserved
                                /*+ InsertionAnalyser.command_declare_property + "|"
                                + InsertionAnalyser.command_intersection + "|"
                                + InsertionAnalyser.command_subclass + "|"
                                + InsertionAnalyser.command_not + "|"
                                + InsertionAnalyser.command_equiv + "|"
                                + InsertionAnalyser.command_relationship + "|"
                                + InsertionAnalyser.command_NEW_qtf_existencial + "|"
                                + InsertionAnalyser.command_NEW_qtf_universal + "|"
                                + InsertionAnalyser.command_declare_entity + "|"*/
                                + ")"
                        )) {
                            setCharacterAttributes(wordL, wordR - wordL, attr_blue, false);
                        } /*else if (text.substring(wordL, wordR).matches(
                         "(\\W)*("
                         + "\\" + InsertionAnalyser.command_qtf_existencial + "|" //adiciono scape
                         + "\\" + InsertionAnalyser.command_qtf_universal + "|"
                         + ">)"//Adiciono manualmente o > e o scape
                         )) {
                         setCharacterAttributes(wordL, wordR - wordL, attr_green, false);
                         } */ else {
                            setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                        }
                        wordL = wordR;
                    }
                    wordR++;
                }
            }

            public void remove(int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) {
                    before = 0;
                }
                int after = findFirstNonWordChar(text, offs);

                String reserved = "";
                for (int i = 0; i < Compilador.RESERVED_WORDS.size(); i++) {
                    reserved += Compilador.RESERVED_WORDS.get(i);

                    if (i + 1 < Compilador.RESERVED_WORDS.size()) {
                        reserved += "|";
                    } else {
                        reserved += "|#";
                    }
                }

                if (text.substring(before, after).matches(
                        "(\\W)*("
                        + reserved
                        /*+ InsertionAnalyser.command_declare_property + "|"
                        + InsertionAnalyser.command_intersection + "|"
                        + InsertionAnalyser.command_subclass + "|"
                        + InsertionAnalyser.command_not + "|"
                        + InsertionAnalyser.command_equiv + "|"
                        + InsertionAnalyser.command_relationship + "|"
                        + InsertionAnalyser.command_NEW_qtf_existencial + "|"
                        + InsertionAnalyser.command_NEW_qtf_universal + "|"
                        + InsertionAnalyser.command_declare_entity + "|"*/
                        + ")"
                )) {
                    setCharacterAttributes(before, after - before, attr_blue, false);
                } /*else if (text.substring(before, after).matches(
                 "(\\W)*("
                 + "\\" + InsertionAnalyser.command_qtf_existencial + "|" //adiciono scape
                 + "\\" + InsertionAnalyser.command_qtf_universal + "|"
                 + ">)"//Adiciono manualmente o > e o scape
                 )) {
                 setCharacterAttributes(before, after - before, attr_green, false);
                 } */ else {
                    setCharacterAttributes(before, after - before, attrBlack, false);
                }
            }
        };
    }

    private int findLastNonWordChar(String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar(String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane(doc);
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextPane1.setFont(fontBasica);
        jTextPane1.setText("main(){\n\tint i;\n\tint j;\n\tboolean flag;\n\n\tprint(1);\n\tprint(true);\n\tprint(false);\n\tprint(i);\n\tprint();\n\t\n\tcall somar(1, 1);\n\tcall somar(i, i);\n\tcall somar(true, true);\n\tcall somar(false, false);\n\tcall somar(1, j);\n\tcall somar(j, 1);\n\tcall somar(true, i);\n\tcall somar(i, false, 2, mateus, 2, Atrue);\n\tcall somar();\n\t\n\tbreak;\n\tcontinue;\n\t\n\ti = 1024;\n\ti = matt;\n\ti = true;\n\ti = [1024 + matt];\n\ti = [(j + matt) * 2048];\n\n\tif(matt == true){\n    } else{\n    \tint antony;\n        antony = [(matt * 1024) / 2];\n    }\n\tif((matt == true && 1 != 2) || flag == true){\n\t}\n}");
        jScrollPane1.setViewportView(jTextPane1);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setText("Compilar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Lista de Tokens");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Console");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Estado da Pilha");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        compilar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String a = "";

        if (compiler.analizadorLexico.tokenArray == null) {
            JOptionPane.showMessageDialog(this, "Nenhum código fonte compilado.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea(compiler.analizadorLexico.getTokenListAsTable());
            textArea.setFont(fontBasica);
            JScrollPane scrollPane = new JScrollPane(textArea);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(500, 500));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, scrollPane, "Lista de Tokens",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        JTextArea textArea = new JTextArea(compiler.errorConsole);
        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(null, scrollPane, "Console",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (compiler.analizadorSintatico.estadoDaPilha == null) {
            JOptionPane.showMessageDialog(this, "Nenhum código fonte compilado.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea(compiler.analizadorSintatico.estadoDaPilha);
            textArea.setFont(fontBasica);
            JScrollPane scrollPane = new JScrollPane(textArea);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(500, 500));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, scrollPane, "Estado da Pilha",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void compilar() {
        jButton2.setEnabled(true);
        jButton4.setEnabled(true);
        jProgressBar1.setValue(0);
        compiler.init(jTextPane1.getText());
        jProgressBar1.setValue(100);

        /* new Thread(new Runnable() {
            @Override
            public void run() {
                jProgressBar1.setValue(0);
            }
        }).start();*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFramePrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
