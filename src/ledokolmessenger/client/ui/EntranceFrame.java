package ledokolmessenger.client.ui;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import ledokolmessenger.serialized.ClientInfo;
import ledokolmessenger.serialized.Respond;

/**
 *
 * @author BOT
 */
public class EntranceFrame extends javax.swing.JFrame {

    private Socket clientSocket;
    private final MessageDigest sha512;
    private final String IP = "localhost";
    private final int PORT = 3443;

    public EntranceFrame() throws NoSuchAlgorithmException {
        initComponents();
        this.signInPasswordField.setForeground(Color.gray);
        sha512 = MessageDigest.getInstance("SHA-512");
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signInPane = new javax.swing.JPanel();
        toRegister = new javax.swing.JButton();
        signInButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        signInLoginField = new javax.swing.JTextField();
        signInPasswordField = new javax.swing.JPasswordField();
        rememberMe = new javax.swing.JCheckBox();
        signInErrorLabel = new javax.swing.JLabel();
        registerPane = new javax.swing.JPanel();
        backToSignIn = new javax.swing.JButton();
        registerButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        registerLoginField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        registerPassword1 = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        registerPassword2 = new javax.swing.JPasswordField();
        errorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new java.awt.CardLayout());

        toRegister.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        toRegister.setText("Зарегистрироваться");
        toRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toRegisterActionPerformed(evt);
            }
        });

        signInButton.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        signInButton.setText("Войти");
        signInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signInButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 26)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Авторизация");

        signInLoginField.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        signInLoginField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        signInLoginField.setText("Логин");
        signInLoginField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                signInLoginFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                signInLoginFieldFocusLost(evt);
            }
        });

        signInPasswordField.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        signInPasswordField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        signInPasswordField.setText("Пароль");
        signInPasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                signInPasswordFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                signInPasswordFieldFocusLost(evt);
            }
        });

        rememberMe.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        rememberMe.setText("Запомнить меня");

        signInErrorLabel.setText(" ");

        javax.swing.GroupLayout signInPaneLayout = new javax.swing.GroupLayout(signInPane);
        signInPane.setLayout(signInPaneLayout);
        signInPaneLayout.setHorizontalGroup(
            signInPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signInPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signInPaneLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(signInPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(signInErrorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rememberMe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(signInPasswordField)
                    .addComponent(signInLoginField)
                    .addComponent(signInButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toRegister, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                .addGap(45, 45, 45))
        );
        signInPaneLayout.setVerticalGroup(
            signInPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, signInPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(signInLoginField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(signInPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rememberMe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(signInErrorLabel)
                .addGap(18, 18, 18)
                .addComponent(signInButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        getContentPane().add(signInPane, "card2");

        backToSignIn.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        backToSignIn.setText("Назад");
        backToSignIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToSignInActionPerformed(evt);
            }
        });

        registerButton.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        registerButton.setText("Зарегистрироваться");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 26)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Регистрация");

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setText("Логин");

        registerLoginField.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        registerLoginField.setToolTipText("Минимальное количество знаков - 6");

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel4.setText("Пароль");

        registerPassword1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        registerPassword1.setToolTipText("Минимальное количество знаков - 6");

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel5.setText("Повторный ввод пароля");

        registerPassword2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        registerPassword2.setToolTipText("Минимальное количество знаков - 6");

        errorLabel.setText(" ");

        javax.swing.GroupLayout registerPaneLayout = new javax.swing.GroupLayout(registerPane);
        registerPane.setLayout(registerPaneLayout);
        registerPaneLayout.setHorizontalGroup(
            registerPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPaneLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(registerPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(registerPassword2)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(registerPassword1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(registerLoginField)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(registerButton, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(backToSignIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(45, 45, 45))
        );
        registerPaneLayout.setVerticalGroup(
            registerPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerLoginField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerPassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(backToSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(registerPane, "card3");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void signInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signInButtonActionPerformed
        try {
            byte[] bytes = sha512.digest(String.valueOf(this.signInPasswordField.getPassword()).getBytes());
            StringBuilder hashPassword = new StringBuilder();
            for (byte b : bytes) {
                hashPassword.append(String.format("%02X", b));
            }
            this.clientSocket = new Socket(IP, PORT);
            this.getClass().toString();
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            oos.writeObject(new ClientInfo("SignIn", this.signInLoginField.getText(), hashPassword.toString()));
            Respond respond = (Respond) ois.readObject();
            if (respond.getRespondCode() == 200) {
                System.out.println(respond.getRespond());
                new MainWindow(clientSocket, oos, ois, this.signInLoginField.getText()).setVisible(true);
                this.setVisible(false);
            } else if (respond.getRespondCode() == 401) {
                this.signInErrorLabel.setForeground(Color.red);
                this.signInErrorLabel.setText(respond.getRespond());
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(EntranceFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_signInButtonActionPerformed

    private void toRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toRegisterActionPerformed
        this.signInPane.setVisible(false);
        this.registerPane.setVisible(true);
    }//GEN-LAST:event_toRegisterActionPerformed

    private void backToSignInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToSignInActionPerformed
        this.registerPane.setVisible(false);
        this.signInPane.setVisible(true);
    }//GEN-LAST:event_backToSignInActionPerformed

    private void signInLoginFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_signInLoginFieldFocusGained
        if ("Логин".equals(this.signInLoginField.getText())) {
            this.signInLoginField.setText(null);
            this.signInLoginField.setForeground(Color.black);
        }
    }//GEN-LAST:event_signInLoginFieldFocusGained

    private void signInLoginFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_signInLoginFieldFocusLost
        if (this.signInLoginField.getText().isEmpty()) {
            this.signInLoginField.setText("Логин");
            this.signInLoginField.setForeground(Color.gray);
        }
    }//GEN-LAST:event_signInLoginFieldFocusLost

    private void signInPasswordFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_signInPasswordFieldFocusGained
        if ("Пароль".equals(String.valueOf(this.signInPasswordField.getPassword()))) {
            this.signInPasswordField.setText(null);
            this.signInPasswordField.setForeground(Color.black);
        }
    }//GEN-LAST:event_signInPasswordFieldFocusGained

    private void signInPasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_signInPasswordFieldFocusLost
        if (String.valueOf(this.signInPasswordField.getPassword()).isEmpty()) {
            this.signInPasswordField.setText("Пароль");
            this.signInPasswordField.setForeground(Color.gray);
        }
    }//GEN-LAST:event_signInPasswordFieldFocusLost

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        try {
            this.errorLabel.setText(" ");
            if (Arrays.equals(this.registerPassword1.getPassword(), this.registerPassword2.getPassword())) {
                byte[] bytes = sha512.digest(String.valueOf(this.registerPassword1.getPassword()).getBytes());
                StringBuilder hashPassword = new StringBuilder();
                for (byte b : bytes) {
                    hashPassword.append(String.format("%02X", b));
                }
                this.clientSocket = new Socket(IP, PORT);
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                oos.writeObject(new ClientInfo("Register", this.registerLoginField.getText(), hashPassword.toString()));
                System.out.println(hashPassword.toString());
                Respond respond = (Respond) ois.readObject();
                System.out.println(respond.getRespondCode() + ": " + respond.getRespond());
                this.errorLabel.setForeground(Color.green);
                this.errorLabel.setText("Вы успешно зарегистрированны!");
                this.registerLoginField.setText("");
                this.registerPassword1.setText("");
                this.registerPassword2.setText("");
                this.clientSocket.close();
                this.clientSocket = null;

                // пользователь с таким именем уже существует
            } else {
                this.errorLabel.setForeground(Color.red);
                this.errorLabel.setText("Пароли отличаются!");
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(EntranceFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_registerButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backToSignIn;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton registerButton;
    private javax.swing.JTextField registerLoginField;
    private javax.swing.JPanel registerPane;
    private javax.swing.JPasswordField registerPassword1;
    private javax.swing.JPasswordField registerPassword2;
    private javax.swing.JCheckBox rememberMe;
    private javax.swing.JButton signInButton;
    private javax.swing.JLabel signInErrorLabel;
    private javax.swing.JTextField signInLoginField;
    private javax.swing.JPanel signInPane;
    private javax.swing.JPasswordField signInPasswordField;
    private javax.swing.JButton toRegister;
    // End of variables declaration//GEN-END:variables
}
