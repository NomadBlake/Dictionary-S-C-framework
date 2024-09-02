/**
 * @author Zhuhan Qin, 988039
 */

package Client;

import javax.swing.JFrame;
import javax.swing.JButton;
import StateCode.StateCode;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ClientUI {

    private JFrame frame;
    private JTextArea meaningPane1;
    private JTextArea meaningPane2;
    private JTextField searchBox1;
    private JTextField searchBox2;
    private Client dictClient;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Create the application.
     */
    public ClientUI(Client client) {
        dictClient = client;
        drawClientUI();
    }

    /**
     * Check the input word and meaning is valid or not.
     */
    private Boolean isValid(String word, String meaning, int command) {
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please Enter a word.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (command == StateCode.ADD && meaning.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please Enter the word's meaning.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (command == StateCode.ADD_MEANING && meaning.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please Enter the meaning to be added.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (command == StateCode.UPDATE && meaning.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please Enter the meaning to be updated.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void drawClientUI() {
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(700, 540));
        frame.setBounds(100, 100, 670, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.DARK_GRAY); // Set frame background color to dark gray

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel queryPanel = createPanel1();
        JPanel editPanel = createPanel2();

        mainPanel.add(queryPanel, "Panel1");
        mainPanel.add(editPanel, "Panel2");

        frame.getContentPane().add(mainPanel);
    }

    private JPanel createPanel1() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);

        searchBox1 = new JTextField();
        searchBox1.setColumns(10);

        JButton addButton = new JButton("ADD");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String word = searchBox1.getText();
                String meaning = meaningPane1.getText();
                if (isValid(word, meaning, StateCode.ADD)) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Confirm to Add a new word?", "Confirm Window", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int state = dictClient.add(word, meaning);
                        if (state == StateCode.UNKNOWN_HOST) {
                            JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.",
                                    "Warning", JOptionPane.ERROR_MESSAGE);
                        } else if (state == StateCode.FAIL) {
                            JOptionPane.showMessageDialog(frame, "Word Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else if (state == StateCode.TIMEOUT) {
                            JOptionPane.showMessageDialog(frame, "Timeout!\nPlease check the server or restart with a correct Address and IP.",
                                    "Warning", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Add Success!", "Tips", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        JButton queryButton = new JButton("Query");
        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String word = searchBox1.getText();
                if (isValid(word, "", StateCode.QUERY)) {
                    String[] resultArr = dictClient.query(word);
                    int state = Integer.parseInt(resultArr[0]);
                    if (state == StateCode.UNKNOWN_HOST) {
                        JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
                    } else if (state == StateCode.FAIL) {
                        JOptionPane.showMessageDialog(frame, "Query Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else if (state == StateCode.TIMEOUT) {
                        JOptionPane.showMessageDialog(frame, "Timeout!\nPlease check the server or restart with a correct Address and IP.",
                                "Warning", JOptionPane.ERROR_MESSAGE);
                    } else {
                        meaningPane1.setText(resultArr[1]);
                    }
                }
            }
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String word = searchBox1.getText();
                if (isValid(word, "", StateCode.REMOVE)) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Confirm to Remove a new word?", "Confirm Window", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int state = dictClient.remove(word);
                        if (state == StateCode.UNKNOWN_HOST) {
                            JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
                        } else if (state == StateCode.FAIL) {
                            JOptionPane.showMessageDialog(frame, "Remove Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else if (state == StateCode.TIMEOUT) {
                            JOptionPane.showMessageDialog(frame, "Timeout!\nPlease check the server or restart with a correct Address and IP.",
                                    "Warning", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Remove Success!", "Tips", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        JButton editMeaningButton = new JButton("Edit Meaning");
        editMeaningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Panel2");
            }
        });

        JLabel lblMeaning = new JLabel("The Meaning(s) of the word: ");
        lblMeaning.setForeground(Color.WHITE); // Set label text color to white

        JScrollPane scrollPane = new JScrollPane();

        JLabel lblWord = new JLabel("Word:");
        lblWord.setForeground(Color.WHITE); // Set label text color to white

        meaningPane1 = new JTextArea();
        scrollPane.setViewportView(meaningPane1);
        meaningPane1.setLineWrap(true);

        GroupLayout groupLayout = new GroupLayout(panel);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblMeaning, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblWord, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(searchBox1, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(addButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(queryButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(removeButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(editMeaningButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblMeaning, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addGap(5)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(lblWord, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(searchBox1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                .addGap(5)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(addButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(queryButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(removeButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(editMeaningButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                .addGap(8))
        );
        panel.setLayout(groupLayout);

        return panel;
    }

    private JPanel createPanel2() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);

        searchBox2 = new JTextField();
        searchBox2.setColumns(10);

        JButton addMeaningButton = new JButton("Add Meaning");
        addMeaningButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String word = searchBox2.getText();
                String meaning = meaningPane2.getText();
                if (isValid(word, meaning, StateCode.ADD_MEANING)) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Confirm to Add new meaning(s)?", "Confirm Window", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int state = dictClient.addMeaning(word, meaning);
                        if (state == StateCode.MEANING_EXIST) {
                            JOptionPane.showMessageDialog(frame, "Meaning already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else if (state == StateCode.UNKNOWN_HOST) {
                            JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
                        } else if (state == StateCode.FAIL) {
                            JOptionPane.showMessageDialog(frame, "Add Meaning Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else if (state == StateCode.TIMEOUT) {
                            JOptionPane.showMessageDialog(frame, "Timeout!\nPlease check the server or restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Add Meaning Success!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String word = searchBox2.getText();
                String newMeaning = meaningPane2.getText();
                if (isValid(word, newMeaning, StateCode.UPDATE)) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Confirm to Update the meaning?", "Confirm Window", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int state = dictClient.update(word, newMeaning);
                        if (state == StateCode.UNKNOWN_HOST) {
                            JOptionPane.showMessageDialog(frame, "Unknown Host!\nPlease restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
                        } else if (state == StateCode.FAIL) {
                            JOptionPane.showMessageDialog(frame, "Update Fail\nWord Not Exist!", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else if (state == StateCode.TIMEOUT) {
                            JOptionPane.showMessageDialog(frame, "Timeout!\nPlease check the server or restart with a correct Address and IP.", "Warning", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Update Success!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Panel1");
            }
        });

        JLabel lblMeaning = new JLabel("The Meaning(s) of the word: ");
        lblMeaning.setForeground(Color.WHITE); // Set label text color to white

        JScrollPane scrollPane = new JScrollPane();

        JLabel lblWord = new JLabel("Word:");
        lblWord.setForeground(Color.WHITE); // Set label text color to white

        meaningPane2 = new JTextArea();
        scrollPane.setViewportView(meaningPane2);
        meaningPane2.setLineWrap(true);

        GroupLayout groupLayout = new GroupLayout(panel);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblMeaning, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblWord, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(searchBox2, GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(addMeaningButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(updateButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(returnButton, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addGap(5))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5)
                                .addComponent(lblMeaning, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addGap(5)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addGap(5)
                                .addComponent(lblWord, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(searchBox2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                .addGap(5)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(addMeaningButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(updateButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(returnButton, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                .addGap(8))
        );
        panel.setLayout(groupLayout);

        return panel;
    }
}










