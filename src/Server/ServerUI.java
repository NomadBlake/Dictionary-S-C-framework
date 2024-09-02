/**
 * @author Zhuhan Qin, 988039
 */
package Server;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class ServerUI {

    private JFrame frame;
    private JTextArea logArea;
    private JLabel addressLabel;
    private JLabel portLabel;
    private JLabel pathLabel;
    private static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font LABEL_FONT = new Font("Helvetica", Font.PLAIN, 12);


    /**
     * Create the application.
     */
    public ServerUI(String address, String port, String path) {
        draw(address, port, path);
    }

    public JFrame getFrame() {
        return frame;
    };

    public JTextArea getlogArea() {
        return logArea;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void draw(String address, String port, String path) {
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(450, 600));
        frame.setBounds(100, 100, 450, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("Server Close!");
            }
        });
        // Se
        // t frame background color to dark gray
        frame.getContentPane().setBackground(Color.DARK_GRAY);

        // Create a scroll pane for the log area
        JScrollPane scrollPane = new JScrollPane();

        logArea = new JTextArea();
        logArea.setLineWrap(true);
        logArea.setEditable(false);
        scrollPane.setViewportView(logArea);

        addressLabel = new JLabel("Address: " + address);
        addressLabel.setForeground(Color.WHITE);

        portLabel = new JLabel("Port: " + port);
        portLabel.setForeground(Color.WHITE);


        pathLabel = new JLabel("Dictionary Path: " + path);
        pathLabel.setForeground(Color.WHITE);

        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(addressLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(portLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(pathLabel, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(20)
                                .addComponent(addressLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(portLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(pathLabel)
                                .addGap(29)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addContainerGap())
        );
        frame.getContentPane().setLayout(groupLayout);
    }

    private JScrollPane createScrollPane() {
        logArea = new JTextArea();
        logArea.setLineWrap(true);
        logArea.setEditable(false);
        logArea.setBackground(BACKGROUND_COLOR);
        logArea.setForeground(TEXT_COLOR);
        JScrollPane scrollPane = new JScrollPane(logArea);
        return scrollPane;
    }
}
