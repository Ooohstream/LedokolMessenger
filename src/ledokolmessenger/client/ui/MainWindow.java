package ledokolmessenger.client.ui;

/*  1) Поправить дублирование при отправлении / получении сообщений в групповых чатах + 
    2) Объединить добавление друзей и список друзей +
    3) Доделать панель сообщений +
    4) Запомнить меня +
    5) РЕФАКТОРИНГ 
 */
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
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

    private String clientName;
    private final Socket clientSocket;
    private final Object lock = new Object();
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Map<String, MessagesPane> scrollPanes = new HashMap<>();
    private Map<String, Boolean> loadedMessageHistory = new HashMap<>();
    private BlockingQueue activities = new BlockingQueue();
    private String groupHash = "ad936fcbed631fa67e05c3ea03953905221c9d46af0616b70badf105a966fb11";

    public MainWindow(Socket clientSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream, String clientName) throws IOException, ClassNotFoundException {
        initComponents();
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        this.jLabel1.setText(this.clientName);
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        try {
            StartupInfo startupInfo = (StartupInfo) inputStream.readObject();

            /* Start screen */
            JScrollPane startScreen = new JScrollPane();
            startScreen.getViewport().setBackground(Color.white);
            startScreen.getViewport().setLayout(new GridBagLayout());
            JLabel startScreenLabel = new JLabel();
            startScreenLabel.setText("LDKL");
            startScreenLabel.setFont(new Font("Verdana", Font.BOLD, 42));
            startScreen.setViewportView(startScreenLabel);
            messagePane.add(startScreen);

            /* Друзья, групповые чаты, запросы на дружбу */
            DefaultListModel<String> friendListModel = (DefaultListModel<String>) friendList.getModel();
            startupInfo.getFriends().forEach(friend -> {
                friendListModel.addElement(friend.getClientName());
                MessagesPane scrollPane = new MessagesPane();
                scrollPane.configure();
                this.messagePane.add(scrollPane, friend.getClientName());
                this.scrollPanes.put(friend.getClientName(), scrollPane);
            });

            DefaultListModel<String> groupListModel = (DefaultListModel<String>) this.groupChatList.getModel();
            startupInfo.getGroupChats().forEach(groupChat -> {
                groupListModel.addElement(groupChat.getClientName());
                MessagesPane scrollPane = new MessagesPane();
                scrollPane.configure();
                this.messagePane.add(scrollPane, groupChat.getClientName() + this.groupHash);
                this.scrollPanes.put(groupChat.getClientName() + this.groupHash, scrollPane);
            });

            startupInfo.getFriendRequests().forEach(friend -> {
                FriendRequestsPane fRP = (FriendRequestsPane) this.friendRequestPane.getViewport().getView();
                fRP.addAnotherPane(friend.getClientName(), this.outputStream, friendListModel);
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
                            scrollPanes.get(message.getSender()).getModel().addElement(message.getSender() + ": " + message.getMessage());
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
                        if (activities.getFirst() != null && activities.getFirst().getType().equals("MessageGroup")) {
                            Message messageGroup = (Message) activities.dequeue();
                            scrollPanes.get(messageGroup.getRecipient() + groupHash).getModel().addElement(messageGroup.getSender() + ": "
                                    + messageGroup.getMessage());
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

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    try (clientSocket; outputStream; inputStream) {
                        outputStream.writeObject(new Message("Message", "##session##end##", null, null, java.time.LocalDateTime.now()));
                        inputStream.close();
                        outputStream.close();
                        clientSocket.close();
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
        exitBtn = new javax.swing.JButton();
        mainMenu = new javax.swing.JTabbedPane();
        friends = new javax.swing.JPanel();
        friendListScrollPane = new javax.swing.JScrollPane();
        friendList = new javax.swing.JList<>();
        addFriendLabel = new javax.swing.JLabel();
        addFriend = new javax.swing.JButton();
        final FriendRequestsPane pane = new FriendRequestsPane();
        friendRequestPane = new javax.swing.JScrollPane(pane);
        groupChatsTabs = new javax.swing.JPanel();
        addGroupChatBtn = new javax.swing.JButton();
        joinGroupChatBtn = new javax.swing.JButton();
        groupChatListScrollPane = new javax.swing.JScrollPane();
        groupChatList = new javax.swing.JList<>();
        groupListLabel = new javax.swing.JLabel();
        messagePane = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Ledokol Messenger");
        setResizable(false);

        messageField.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        messageField.setText(" ");
        messageField.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        messageField.setVisible(false);

        sendButton.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        sendButton.setText("Отправить");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        sendButton.setVisible(false);

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Name");

        exitBtn.setText("Выход");
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exitBtn)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addComponent(exitBtn))
                .addContainerGap())
        );

        DefaultListModel<String> friendListModel = new DefaultListModel<>();
        friendList.setModel(friendListModel);
        friendList.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        friendList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                friendListValueChanged(evt);
            }
        });
        friendListScrollPane.setViewportView(friendList);

        addFriendLabel.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        addFriendLabel.setText(" ");

        addFriend.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        addFriend.setText("Добавить друга");
        addFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFriendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout friendsLayout = new javax.swing.GroupLayout(friends);
        friends.setLayout(friendsLayout);
        friendsLayout.setHorizontalGroup(
            friendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(friendsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(friendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addFriendLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addFriend, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .addComponent(friendListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        friendsLayout.setVerticalGroup(
            friendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, friendsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addFriendLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addFriend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(friendListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainMenu.addTab("Друзья", friends);
        mainMenu.addTab("Запросы в друзья", friendRequestPane);

        addGroupChatBtn.setText("Создать групповой чат");
        addGroupChatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroupChatBtnActionPerformed(evt);
            }
        });

        joinGroupChatBtn.setText("Вступить в групповой чат");
        joinGroupChatBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinGroupChatBtnActionPerformed(evt);
            }
        });

        DefaultListModel<String> groupChatListModel = new DefaultListModel<>();
        groupChatList.setModel(groupChatListModel);
        groupChatList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                groupChatListValueChanged(evt);
            }
        });
        groupChatListScrollPane.setViewportView(groupChatList);

        groupListLabel.setText(" ");

        javax.swing.GroupLayout groupChatsTabsLayout = new javax.swing.GroupLayout(groupChatsTabs);
        groupChatsTabs.setLayout(groupChatsTabsLayout);
        groupChatsTabsLayout.setHorizontalGroup(
            groupChatsTabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupChatsTabsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupChatsTabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addGroupChatBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(joinGroupChatBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .addComponent(groupChatListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(groupListLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        groupChatsTabsLayout.setVerticalGroup(
            groupChatsTabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupChatsTabsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(groupListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addGroupChatBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(joinGroupChatBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(groupChatListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainMenu.addTab("Групповые чаты", groupChatsTabs);

        messagePane.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainMenu)
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
                if (this.mainMenu.getSelectedIndex() == 0) {
                    String recipient = this.friendList.getSelectedValue();
                    Message message = new Message("Message", this.messageField.getText(), this.clientName, recipient, java.time.LocalDateTime.now());
                    this.outputStream.writeObject(message);
                    scrollPanes.get(recipient).getModel().addElement(message.getSender() + ": " + message.getMessage());
                } else if (this.mainMenu.getSelectedIndex() == 2) {
                    String recipient = this.groupChatList.getSelectedValue();
                    Message message = new Message("MessageGroup", this.messageField.getText(), this.clientName, recipient, java.time.LocalDateTime.now());
                    this.outputStream.writeObject(message);
                    scrollPanes.get(recipient + groupHash).getModel().addElement(message.getSender() + ": " + message.getMessage());
                }
                this.messageField.setText(" ");
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void groupChatListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_groupChatListValueChanged
        if (!evt.getValueIsAdjusting()) {
            String selectedName = this.groupChatList.getSelectedValue();
            try {
                if (!this.loadedMessageHistory.containsKey(selectedName + this.groupHash)) {
                    ClientInfo group = new ClientInfo("getGroupMessageHistory", selectedName);
                    outputStream.writeObject(group);
                    synchronized (lock) {
                        lock.wait();
                    }
                    MessageList activity;
                    if (activities.getFirst() != null && activities.getFirst().getType().equals("GroupMessageHistory")) {
                        activity = (MessageList) activities.dequeue();
                        if (activity.getMessageList() != null) {
                            activity.getMessageList().forEach(message -> {
                                scrollPanes.get(selectedName + groupHash).getModel().addElement(message.getSender() + ": " + message.getMessage());
                            });
                        }

                    }
                    this.loadedMessageHistory.put(selectedName + groupHash, true);
                }
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            CardLayout l = (CardLayout) this.messagePane.getLayout();
            l.show(this.messagePane, this.groupChatList.getSelectedValue() + this.groupHash);
            if (!this.sendButton.isVisible()) {
                this.sendButton.setVisible(true);
                this.messageField.setVisible(true);
            }
        }
    }//GEN-LAST:event_groupChatListValueChanged

    private void joinGroupChatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinGroupChatBtnActionPerformed
        JFrame frame = new JFrame();
        String text = JOptionPane.showInputDialog(frame, "Введите имя чата: ");
        if (text != null) {
            try {
                this.outputStream.writeObject(new ClientInfo("joinGroup", text));
                synchronized (lock) {
                    lock.wait();
                }
                if (this.activities.getFirst() != null && this.activities.getFirst().getType().equals("Respond")) {
                    Respond respond = (Respond) activities.dequeue();
                    switch (respond.getRespondCode()) {
                        case 404:
                            this.groupListLabel.setText(respond.getRespond());
                            this.groupListLabel.setForeground(Color.red);
                            break;
                        case 200:
                            this.groupListLabel.setText(respond.getRespond());
                            this.groupListLabel.setForeground(Color.green);
                            DefaultListModel<String> model = (DefaultListModel<String>) this.groupChatList.getModel();
                            model.addElement(text);
                            MessagesPane scrollPane = new MessagesPane();
                            scrollPane.configure();
                            this.messagePane.add(scrollPane, text + this.groupHash);
                            this.scrollPanes.put(text + this.groupHash, scrollPane);
                            break;
                        case 400:
                            this.groupListLabel.setText(respond.getRespond());
                            this.groupListLabel.setForeground(Color.yellow);
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_joinGroupChatBtnActionPerformed

    private void addGroupChatBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroupChatBtnActionPerformed
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
                    } else if (respond.getRespondCode() == 201) {
                        this.groupListLabel.setText(respond.getRespond());
                        this.groupListLabel.setForeground(Color.green);
                        DefaultListModel<String> model = (DefaultListModel<String>) this.groupChatList.getModel();
                        model.addElement(text);
                        MessagesPane scrollPane = new MessagesPane();
                        scrollPane.configure();
                        this.messagePane.add(scrollPane, text + this.groupHash);
                        this.scrollPanes.put(text + this.groupHash, scrollPane);
                    }
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_addGroupChatBtnActionPerformed

    private void addFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFriendActionPerformed
        JFrame frame = new JFrame();
        String text = JOptionPane.showInputDialog(frame, "Введите имя друга: ");
        if (text != null) {
            ClientInfo newFriend = new ClientInfo("addFriend", text);
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
        }
    }//GEN-LAST:event_addFriendActionPerformed

    private void friendListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_friendListValueChanged
        if (!evt.getValueIsAdjusting()) {
            String selectedName = this.friendList.getSelectedValue();
            try {
                if (!this.loadedMessageHistory.containsKey(selectedName)) {
                    ClientInfo user = new ClientInfo("getMessageHistory", selectedName);
                    outputStream.writeObject(user);
                    synchronized (lock) {
                        lock.wait();
                    }
                    MessageList activity;
                    if (activities.getFirst() != null && activities.getFirst().getType().equals("MessageHistory")) {
                        activity = (MessageList) activities.dequeue();
                        if (activity.getMessageList() != null) {
                            activity.getMessageList().forEach(message -> {
                                scrollPanes.get(selectedName).getModel().addElement(message.getSender() + ": " + message.getMessage());
                            });
                        }

                    }
                    this.loadedMessageHistory.put(selectedName, true);
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

    private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.clientSocket.close();
            File file = new File("rememberMe.properties");

            if (Files.deleteIfExists(file.toPath())) {
                System.out.println("rememberMeCleared");
            }
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exitBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFriend;
    private javax.swing.JLabel addFriendLabel;
    private javax.swing.JButton addGroupChatBtn;
    private javax.swing.JButton exitBtn;
    private javax.swing.JList<String> friendList;
    private javax.swing.JScrollPane friendListScrollPane;
    private javax.swing.JScrollPane friendRequestPane;
    private javax.swing.JPanel friends;
    private javax.swing.JList<String> groupChatList;
    private javax.swing.JScrollPane groupChatListScrollPane;
    private javax.swing.JPanel groupChatsTabs;
    private javax.swing.JLabel groupListLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton joinGroupChatBtn;
    private javax.swing.JTabbedPane mainMenu;
    private javax.swing.JTextField messageField;
    private javax.swing.JPanel messagePane;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
