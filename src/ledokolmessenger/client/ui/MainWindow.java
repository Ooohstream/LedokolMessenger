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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import ledokolmessenger.client.BlockingQueue;
import ledokolmessenger.serialized.*;

/**
 *
 * @author BOT
 */
public class MainWindow extends javax.swing.JFrame {

    String clientName;
    private final Socket clientSocket;
    private final Object lock = new Object();
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Map<String, MessagesPane> scrollPanes = new HashMap<>();
    private Map<String, Boolean> gotOldMessages = new HashMap<>();
    BlockingQueue activities = new BlockingQueue();

    public MainWindow(Socket clientSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream, String clientName) throws IOException, ClassNotFoundException {
        initComponents();
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        this.jLabel1.setText(this.clientName);
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        try {
            DefaultListModel<String> model = new DefaultListModel<>();
            this.friendList.setModel(model);
            List<ClientInfo> friends = (List<ClientInfo>) inputStream.readObject();
            List<ClientInfo> friendRequests = (List<ClientInfo>) inputStream.readObject();
            List<ClientInfo> groups = (List<ClientInfo>) inputStream.readObject();
            this.messageField.setVisible(false);
            this.sendButton.setVisible(false);
            JScrollPane startScreen = new JScrollPane();
            startScreen.setBackground(Color.white);
            JLabel startScreenLabel = new JLabel();
            startScreenLabel.setText("LDKL");
            startScreen.setViewportView(startScreenLabel);
            messagePane.add(startScreen);
            friends.forEach(friend -> {
                model.addElement(friend.getClientName());
                MessagesPane scrollPane = new MessagesPane();
                scrollPane.configure();
                this.messagePane.add(scrollPane, friend.getClientName());
                this.scrollPanes.put(friend.getClientName(), scrollPane);
            });
            friendRequests.forEach(friend -> {
                FriendRequestsPane fRP = (FriendRequestsPane) this.friendRequestPane.getViewport().getView();
                fRP.addAnotherPane(friend.getClientName(), this.outputStream, model);
            });
            DefaultListModel<String> groupListModel = (DefaultListModel<String>) this.groupChatList.getModel();
            groups.forEach(group -> {
                groupListModel.addElement(group.getClientName());
            });
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread receiver = new Thread(() -> {
            try {
                while (true) {
                    Queue<SendableObject> respond = (LinkedList<SendableObject>) inputStream.readObject();
                    respond.forEach(e -> {
                        activities.enqueue(e);
                    });
                    synchronized (lock) {
                        lock.notifyAll();
                    }
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

        Thread dynamicAcitivitiesServer = new Thread(() -> {
            while (true) {
                try {
                    if (!activities.isEmpty()) {
                        if (activities.getFirst() != null && activities.getFirst().getType().equals("Message")) {
                            Message message = (Message) activities.dequeue();
                            scrollPanes.get(message.getSender()).getModel().addElement(message.getSender() + ": "
                                    + message.getMessage());
                        }
                        if (activities.getFirst() != null && activities.getFirst().getType().equals("approveRequestFriend")) {
                            Message approvalInfo = (Message) activities.dequeue();
                            DefaultListModel<String> model = (DefaultListModel<String>) this.friendList.getModel();
                            model.addElement(approvalInfo.getSender());
                            MessagesPane scrollPane = new MessagesPane();
                            scrollPane.configure();
                            this.messagePane.add(scrollPane, approvalInfo.getSender());
                            this.scrollPanes.put(approvalInfo.getSender(), scrollPane);
                        }
                        if (activities.getFirst() != null && activities.getFirst().getType().equals("RequestFriend")) {
                            Message requestInfo = (Message) activities.dequeue();
                            FriendRequestsPane fRP = (FriendRequestsPane) this.friendRequestPane.getViewport().getView();
                            fRP.addAnotherPane(requestInfo.getSender(), this.outputStream, (DefaultListModel<String>) friendList.getModel());
                            MessagesPane scrollPane = new MessagesPane();
                            scrollPane.configure();
                            this.messagePane.add(scrollPane, requestInfo.getSender());
                            this.scrollPanes.put(requestInfo.getSender(), scrollPane);
                        }
                    }
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        receiver.start();
        dynamicAcitivitiesServer.start();

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
        friendListScrollPane = new javax.swing.JScrollPane();
        friendList = new javax.swing.JList<>();
        addFriendPanel = new javax.swing.JPanel();
        friendNameTextField = new javax.swing.JTextField();
        addFriend = new javax.swing.JButton();
        addFriendLabel = new javax.swing.JLabel();
        final FriendRequestsPane pane = new FriendRequestsPane();
        friendRequestPane = new javax.swing.JScrollPane(pane);
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        groupChatListScrollPane = new javax.swing.JScrollPane();
        groupChatList = new javax.swing.JList<>();
        groupListLabel = new javax.swing.JLabel();
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
                .addContainerGap(122, Short.MAX_VALUE)
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

        friendList.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        friendList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                friendListValueChanged(evt);
            }
        });
        friendListScrollPane.setViewportView(friendList);

        mainMenu.addTab("Друзья", friendListScrollPane);

        addFriendPanel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        addFriendPanel.setMaximumSize(new java.awt.Dimension(100, 100));

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

        javax.swing.GroupLayout addFriendPanelLayout = new javax.swing.GroupLayout(addFriendPanel);
        addFriendPanel.setLayout(addFriendPanelLayout);
        addFriendPanelLayout.setHorizontalGroup(
            addFriendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addFriendPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addFriendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(friendNameTextField)
                    .addComponent(addFriend, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .addComponent(addFriendLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        addFriendPanelLayout.setVerticalGroup(
            addFriendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addFriendPanelLayout.createSequentialGroup()
                .addContainerGap(126, Short.MAX_VALUE)
                .addComponent(addFriendLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(friendNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addFriend)
                .addGap(115, 115, 115))
        );

        mainMenu.addTab("Добавить друга", addFriendPanel);
        mainMenu.addTab("Запросы в друзья", friendRequestPane);

        jButton1.setText("Создать групповой чат");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Вступить в групповой чат");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        DefaultListModel<String> groupChatListModel = new DefaultListModel<>();
        groupChatList.setModel(groupChatListModel);
        groupChatListScrollPane.setViewportView(groupChatList);

        groupListLabel.setText(" ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .addComponent(groupChatListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(groupListLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(groupChatListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainMenu.addTab("Групповые чаты", jPanel2);

        messagePane.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                    .addComponent(messagePane, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            try {
                String recipient = this.friendList.getSelectedValue();
                Message message = new Message("Message", this.messageField.getText(), this.clientName, recipient, java.time.LocalDateTime.now());
                this.outputStream.writeObject(message);
                scrollPanes.get(recipient).getModel().addElement(message.getMessage());
                this.messageField.setText(" ");
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void addFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFriendActionPerformed
        ClientInfo newFriend = new ClientInfo("addFriend", this.friendNameTextField.getText());
        try {
            outputStream.writeObject(newFriend);
            synchronized (lock) {
                lock.wait();
            }
            Respond respond;
            if (activities.getFirst().getType().equals("Respond")) {
                respond = (Respond) activities.dequeue();
                System.out.println(respond.getRespondCode());
                if (respond.getRespondCode() == 404) {
                    addFriendLabel.setText(respond.getRespond());
                }
                if (respond.getRespondCode() == 200) {
                    addFriendLabel.setText("Заявка на дружбу отправлена");

                }
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addFriendActionPerformed

    private void friendListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_friendListValueChanged
        if (!evt.getValueIsAdjusting()) {
            String selectedName = this.friendList.getSelectedValue();
            try {
                if (!this.gotOldMessages.containsKey(selectedName)) {
                    ClientInfo user = new ClientInfo("getOldMessages", selectedName);
                    outputStream.writeObject(user);
                    synchronized (lock) {
                        lock.wait();
                    }
                    MessageList activity;
                    if (activities.getFirst().getType().equals("OldMessages")) {
                        activity = (MessageList) activities.dequeue();
                        if (activity.getMessageList() != null) {
                            activity.getMessageList().forEach(message -> {
                                scrollPanes.get(selectedName).getModel().addElement(message.getMessage());
                            });
                        }

                    }
                    this.gotOldMessages.put(selectedName, true);
                }
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            CardLayout l = (CardLayout) this.messagePane.getLayout();
            l.show(this.messagePane, this.friendList.getSelectedValue());
            if (!this.sendButton.isVisible()) {
                this.sendButton.setVisible(true);
                this.messageField.setVisible(true);
            }
        }
    }//GEN-LAST:event_friendListValueChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFrame frame = new JFrame();
        String text = JOptionPane.showInputDialog(frame, "Ввидет имя чата: ");
        if (text != null) {
            try {
                this.outputStream.writeObject(new ClientInfo("createGroup", text));
                synchronized (lock) {
                    lock.wait();
                }
                if (this.activities.getFirst() != null && this.activities.getFirst().getType().equals("Respond")) {
                    Respond respond = (Respond) activities.dequeue();
                    if (respond.getRespondCode() == 404) {
                        this.groupListLabel.setText(respond.getRespond());
                        this.groupListLabel.setForeground(Color.red);
                    } else if (respond.getRespondCode() == 200) {
                        this.groupListLabel.setText(respond.getRespond());
                        this.groupListLabel.setForeground(Color.green);
                        DefaultListModel<String> model = (DefaultListModel<String>) this.groupChatList.getModel();
                        model.addElement(text);
                    }
                }
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFrame frame = new JFrame();
        String text = JOptionPane.showInputDialog(frame, "Введите имя чата: ");
        if (text != null) {
            System.out.println(text);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFriend;
    private javax.swing.JLabel addFriendLabel;
    private javax.swing.JPanel addFriendPanel;
    private javax.swing.JList<String> friendList;
    private javax.swing.JScrollPane friendListScrollPane;
    private javax.swing.JTextField friendNameTextField;
    private javax.swing.JScrollPane friendRequestPane;
    private javax.swing.JList<String> groupChatList;
    private javax.swing.JScrollPane groupChatListScrollPane;
    private javax.swing.JLabel groupListLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane mainMenu;
    private javax.swing.JTextField messageField;
    private javax.swing.JPanel messagePane;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
