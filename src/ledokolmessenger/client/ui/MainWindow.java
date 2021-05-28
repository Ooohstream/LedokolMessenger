package ledokolmessenger.client.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import ledokolmessenger.client.BlockingQueue;
import ledokolmessenger.serialized.*;

/**
 *
 * @author BOT
 */
public class MainWindow extends javax.swing.JFrame {

    String clientName;
    private final Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final Object lock;
    private Map<String, JScrollPane> scrollPanes = new HashMap<>();
    private Map<String, Boolean> gotOldMessages = new HashMap<>();
    BlockingQueue activities = new BlockingQueue();

    private JScrollPane getMyMessageTable() {
        MessagesScrollPane scrollPane = new MessagesScrollPane();
        scrollPane.configure();
        return scrollPane;
    }

    public MainWindow(Socket clientSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream, String clientName) throws IOException, ClassNotFoundException {
        initComponents();
        this.lock = new Object();
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        this.jLabel1.setText(this.clientName);
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        try {
            DefaultListModel<String> model = new DefaultListModel<>();
            this.jList1.setModel(model);
            List<ClientInfo> friends = (List<ClientInfo>) inputStream.readObject();
            JPanel startScreen = new JPanel();
            JLabel label = new JLabel("LDKL");
            startScreen.add(label);
            startScreen.setBackground(Color.WHITE);
            this.messageField.setVisible(false);
            this.sendButton.setVisible(false);
            this.messagePane.add(startScreen, "StartScreen");
            friends.forEach(friend -> {
                model.addElement(friend.getClientName());
                JScrollPane newScrollPane = this.getMyMessageTable();
                this.messagePane.add(newScrollPane, friend.getClientName());
                this.scrollPanes.put(friend.getClientName(), newScrollPane);
            });
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread receiver = new Thread(() -> {
            try {
                while (true) {
                    Queue<SendableObject> respond = (Queue<SendableObject>) this.inputStream.readObject();
                    respond.forEach(e -> {
                        System.out.println(e.getType());
                        activities.enqueue(e);
                    });

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                try {
                    this.inputStream.close();
                    this.outputStream.close();
                    this.clientSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        receiver.start();

//        Timer timer = new Timer();
//
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (!activities.isEmpty()) {
//                    SendableObject activity = activities.remove();
//                    System.out.println("Hello");
//                }
//            }
//        }, 0, 100);
        Thread activityServer = new Thread(() -> {
            while (true) {
                SendableObject activity = activities.dequeue();
                System.out.println(activity.getType());
            }
        });

        activityServer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    try (clientSocket; outputStream; inputStream) {
                        outputStream.writeObject(new Message("Message", "##session##end##", null, null, java.time.LocalDateTime.now()));
                    }
                } catch (IOException exc) {
                    System.out.println("Closed");
                }
                super.windowClosing(e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        messageField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        mainMenu = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        friendNameTextField = new javax.swing.JTextField();
        addFriend = new javax.swing.JButton();
        addFriendLabel = new javax.swing.JLabel();
        messagePane = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        messageField.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        messageField.setText(" ");
        messageField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        sendButton.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        sendButton.setText("Отправить");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        jList1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        mainMenu.addTab("Друзья", jScrollPane2);

        jScrollPane3.setBorder(null);

        jList2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jScrollPane3.setViewportView(jList2);

        mainMenu.addTab("Групповые чаты", jScrollPane3);

        jPanel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jPanel2.setMaximumSize(new java.awt.Dimension(100, 100));

        friendNameTextField.setFont(new java.awt.Font("Verdana", 0, 16)); // NOI18N
        friendNameTextField.setBorder(null);

        addFriend.setFont(new java.awt.Font("Verdana", 0, 16)); // NOI18N
        addFriend.setText("Добавить");
        addFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFriendActionPerformed(evt);
            }
        });

        addFriendLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        addFriendLabel.setText(" ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(friendNameTextField)
                    .addComponent(addFriend, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(addFriendLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(131, Short.MAX_VALUE)
                .addComponent(addFriendLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(friendNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addFriend)
                .addGap(115, 115, 115))
        );

        mainMenu.addTab("Добавить друга", jPanel2);

        messagePane.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                    .addComponent(messagePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messagePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(messageField)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mainMenu)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        if (!this.messageField.getText().trim().isEmpty()) {
            JScrollPane scrollPane = scrollPanes.get(this.jList1.getSelectedValue());
            JTable jTable = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();
            try {
                Message message = new Message("Message", this.messageField.getText(), this.clientName, this.jList1.getSelectedValue(), java.time.LocalDateTime.now());
                this.outputStream.writeObject(message);
                model.addRow(new Object[]{this.clientName, message.getMessage(), " ", " "});
                this.messageField.setText(" ");
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void addFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFriendActionPerformed
        ClientInfo newFriend = new ClientInfo("addFriend", this.friendNameTextField.getText());
        try {
            outputStream.writeObject(newFriend);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addFriendActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        if (!evt.getValueIsAdjusting()) {
            try {
                if (!this.gotOldMessages.containsKey(this.jList1.getSelectedValue())) {
                    ClientInfo user = new ClientInfo("getOldMessages", this.jList1.getSelectedValue());
                    outputStream.writeObject(user);
                    this.gotOldMessages.put(this.jList1.getSelectedValue(), true);
                }
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            CardLayout l = (CardLayout) this.messagePane.getLayout();
            l.show(this.messagePane, this.jList1.getSelectedValue());
            JScrollPane scrollPane = scrollPanes.get(this.jList1.getSelectedValue());
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

            if (!this.sendButton.isVisible()) {
                this.sendButton.setVisible(true);
                this.messageField.setVisible(true);
            }
        }
    }//GEN-LAST:event_jList1ValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFriend;
    private javax.swing.JLabel addFriendLabel;
    private javax.swing.JTextField friendNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane mainMenu;
    private javax.swing.JTextField messageField;
    private javax.swing.JPanel messagePane;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
