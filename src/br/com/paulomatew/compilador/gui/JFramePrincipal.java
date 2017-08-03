package br.com.paulomatew.compilador.gui;

import br.com.paulomatew.compilador.entities.Escopo;
import static br.com.paulomatew.compilador.entities.OSValidator.isUnix;
import br.com.paulomatew.compilador.main.Compilador;
import com.sun.glass.events.KeyEvent;
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
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class JFramePrincipal extends javax.swing.JFrame {

    /*x.x.x
    1º X = gramática
    2º X = minor changes
    3º X = gui
     */
    private String title = "Compilador (P.Mateus): 7.1.3";

    private DefaultStyledDocument doc;
    public javax.swing.JTextPane editor;

    public Compilador compiler = null;
    public ImageIcon iconExecute, iconList, circleRed, circleGreen;

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
        circleRed = new ImageIcon("./images/circle_red.png");
        circleGreen = new ImageIcon("./images/circle_green.png");

        initEditor();

        initComponents();
        jButton1.setMnemonic(KeyEvent.VK_F5);
        setTitle(title);
        jCircle.setVisible(false);

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
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jCircle = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(860, 620));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jTextPane1.setFont(fontBasica);
        jTextPane1.setText("#main(){\n#    int i;\n#    int j;\n#    boolean flag;\n#\n#    print(1);\n#    print(true);\n#    print(false);\n#    print(i);\n#    print();\n#    \n#    call somar(1, 1);\n#    call somar(i, i);\n#    call somar(true, true);\n#    call somar(false, false);\n#    call somar(1, j);\n#    call somar(j, 1);\n#    call somar(true, i);\n#    call somar(i, false, 2, mateus, 2, true);\n#    call somar();\n#    \n#    break;\n#    continue;\n#    \n#    i = 1024;\n#    i = matt;\n#    i = true;\n#    i = [1024 + matt];\n#    i = [(j + matt) * 2048];\n#\n#    if(matt == true){\n#    } else{\n#        int antony;\n#        antony = [(matt * 1024) / 2];\n#    }\n#    if((matt == true && 1 != 2) || flag == true){\n#    }\n#\n#    while(true){\n#        break;\n#    }\n#\n#    if(m == true){\n#        int i;\n#        int j;\n#        i = 30;\n#        j = [i * 50];\n#    } else{\n#        int antony;\n#        antony = [(m * 1024) / 2];\n#    }\n#\n#   n = 35;\n#   m = call aeee1(n, flag, 1, true);\n#   flag = call aeee2(flag, n, true, 1);\n#}\n#function int aeee(int flag1, boolean flag2){\n#    return [flag1+2];\n#}\n#function boolean aeee2(int flag1, boolean flag2){\n#    return (flag2 == 1) && true !=flag2;\n#}\n#function void aeee2(int flag1, boolean flag2){\n#}\n#\n#main(){\n#    int m;\n#    int n;\n#    boolean flag;\n#    boolean flag2;\n#    n = [1024 + m];\n#    n = [(n + m) * 2048/4];\n#    flag =  true == false && false == false;\n#\n#    if ((flag2 == true && true != false && flag2 == flag) || 1 == 2 || m > 25 || m > n){}\n#    while ((flag2 == true && true != false && flag2 == flag) || 1 == 2 || m > 25 || m > n){}\n#    flag2 =  ((flag2 == true && true != false && flag2 == flag) || 1 == 2 || m > 25 || m > n);\n#\n#}\n#function int aeee1(int flag1, boolean flag2, int flag3, boolean flag4){\n#    return [flag1+2];\n#}\n#function boolean aeee2(boolean flag1, int flag2, boolean flag3, int flag4){\n#    return (flag2 == 1) && true !=flag1;\n#}\n#function boolean aeee4(boolean flag1){\n#    return flag1;\n#}\n#function void aeee3(){\n#    int j;\n#    j = 25;\n#}\n#main(){\n#    boolean flag;\n#    if((flag==true) && true==true){\n#        int i;\n#        boolean j;\n#        j = true;\n#        j = j != j;\n#        j = true!=false && (true==true || false==false);\n#        i = 20;\n#        i = [((2+2)/9)+1*(3+3)-1];\n#\n#        #i = [2-2];\n#        #i = [2*2];\n#        #i = [2/2];\n#        call f();\n#        i = call m(i,22);\n#\n#        if (true){}\n#        if (false){} else{}\n#    } else {\n#        int j;\n#        j = 1500;\n#    }\n#}\nmain(){\n    boolean flag;\n    if(flag){\n        if(true){\n            int x;\n            x = 1500;\n        } else {\n            int x;\n            x = 9999;\n        }\n    }\n    \n    int i;\n    int j;\n    int x;\n    i = 1;\n    j = 10;\n    x = 999;\n    while(i < 10){\n        i = [i + 1];\n\n        while(x > 1000){\n            x = [x+1];\n\n            if(x==1000){\n                break;\n            }\n        }\n    }\n    boolean flag1;\n    flag1 = true;\n}\n\nfunction int m(int a, int b){return 1;}\nfunction boolean f(){return false;}");
        jTextPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextPane1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextPane1);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setText("Compilar (F5)");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        jButton2.setText("Lista de Tokens (F1)");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listarTokensActionPerformed(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton2KeyPressed(evt);
            }
        });

        jButton3.setText("Console (F6)");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consoleActionPerformed(evt);
            }
        });
        jButton3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton3KeyPressed(evt);
            }
        });

        jButton4.setText("Estado da Pilha (F3)");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estadoPilhaActionPerformed(evt);
            }
        });
        jButton4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton4KeyPressed(evt);
            }
        });

        jCircle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jCircle.setPreferredSize(new java.awt.Dimension(93, 20));

        jButton5.setText("Código 3 endereços (F4)");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codigo3EnderecosActionPerformed(evt);
            }
        });
        jButton5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton5KeyPressed(evt);
            }
        });

        jButton6.setText("Árvore de Escopos (F2)");
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arvoreEscoposActionPerformed(evt);
            }
        });
        jButton6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton6KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCircle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton6)
                        .addComponent(jButton5))
                    .addComponent(jCircle, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        compilar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void listarTokensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listarTokensActionPerformed
        String a = "";

        if (compiler.analizadorLexico.tokenArray == null) {
            JOptionPane.showMessageDialog(this, "Nenhum código fonte compilado.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea(compiler.analizadorLexico.getTokenListAsTable());
            textArea.setFont(fontBasica);
            JScrollPane scrollPane = new JScrollPane(textArea);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(1000, 500));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, scrollPane, "Lista de Tokens",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_listarTokensActionPerformed

    private void consoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consoleActionPerformed
        JTextArea textArea = new JTextArea(compiler.errorConsole);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setFont(fontBasica);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(null, scrollPane, "Console",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_consoleActionPerformed

    private void estadoPilhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estadoPilhaActionPerformed

        if (compiler.analizadorSintatico.stackState == null) {
            JOptionPane.showMessageDialog(this, "Nenhum código fonte compilado.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea(compiler.analizadorSintatico.stackState);
            textArea.setFont(fontBasica);
            JScrollPane scrollPane = new JScrollPane(textArea);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(500, 500));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, scrollPane, "Estado da Pilha",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_estadoPilhaActionPerformed

    private void jTextPane1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextPane1KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jTextPane1KeyPressed

    private void codigo3EnderecosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codigo3EnderecosActionPerformed

        if (compiler.codigoIntermediario == null || compiler.codigoIntermediario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum código fonte compilado.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JTextArea textArea = new JTextArea(compiler.codigoIntermediario);
            textArea.setFont(fontBasica);
            JScrollPane scrollPane = new JScrollPane(textArea);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(700, 500));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, scrollPane, "Código intermediário",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_codigo3EnderecosActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        atalhos(evt);
    }//GEN-LAST:event_formKeyPressed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jButton3KeyPressed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton4KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jButton4KeyPressed

    private void jButton5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton5KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jButton5KeyPressed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jButton1KeyPressed

    private void arvoreEscoposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arvoreEscoposActionPerformed
        if (compiler.analizadorLexico.escoposArvore == null) {
            JOptionPane.showMessageDialog(this, "Nenhum código fonte compilado.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String a = "";
            for (Escopo in : compiler.analizadorLexico.escoposArvore) {
                a += in.getData();
                //in.print();
            }

            JTextArea textArea = new JTextArea(a);
            textArea.setFont(fontBasica);
            JScrollPane scrollPane = new JScrollPane(textArea);

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(700, 500));
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, scrollPane, "Árvore de Escopos",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_arvoreEscoposActionPerformed

    private void jButton6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton6KeyPressed
        atalhos(evt);
    }//GEN-LAST:event_jButton6KeyPressed

    private void atalhos(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F5) {
            compilar();
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F4) {
            codigo3EnderecosActionPerformed(null);
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F3) {
            estadoPilhaActionPerformed(null);
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F2) {
            arvoreEscoposActionPerformed(null);
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F1) {
            listarTokensActionPerformed(null);
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F6) {
            consoleActionPerformed(null);
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    private void compilar() {
        jButton2.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton6.setEnabled(true);
        //jProgressBar1.setValue(0);
        compiler.init(jTextPane1.getText());
        //jProgressBar1.setValue(100);

        if (compiler.erro) {
            jCircle.setIcon(circleRed);
            jCircle.setText("Error");
        } else {
            jCircle.setIcon(circleGreen);
            jCircle.setText("Compiled");
        }
        jCircle.setVisible(true);
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
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jCircle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
