package ledokolmessenger.client.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import ledokolmessenger.serialized.ClientInfo;

/**
 *
 * @author OMEN
 */
public class FriendRequestsPane extends JPanel {

    private JPanel filler;
    private final Map<String, JPanel> requests = new HashMap<>();
    private int y = 0;

    public FriendRequestsPane() {

        setLayout(new GridBagLayout());

        filler = new JPanel();
        filler.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.gridy = 0;

        add(filler, gbc);

    }

    public void addAnotherPane(String friendName, ObjectOutputStream outputStream, DefaultListModel<String> friendList) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(2, 2, 2, 2);
        panel.add(new JLabel(friendName, SwingConstants.CENTER), c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;

        JButton addButton = new JButton("Добавить");
        addButton.addActionListener((ActionEvent evt) -> {
            try {
                friendList.addElement(friendName);
                outputStream.writeObject(new ClientInfo("approveFriend", friendName, true));
                remove(panel);
                revalidate();
                repaint();
            } catch (IOException ex) {
                Logger.getLogger(FriendRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        panel.add(addButton, c);

        c.gridx = 1;

        JButton declineButton = new JButton("Отклонить");
        declineButton.addActionListener((ActionEvent evt) -> {
            try {
                outputStream.writeObject(new ClientInfo("approveFriend", friendName, false));
                remove(panel);
                revalidate();
                repaint();
            } catch (IOException ex) {
                Logger.getLogger(FriendRequestsPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        panel.add(declineButton, c);

        panel.revalidate();

        gbc.gridy = y++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(4, 4, 4, 4);

        add(panel, gbc);

        GridBagLayout gbl = ((GridBagLayout) getLayout());
        gbc = gbl.getConstraints(filler);
        gbc.gridy = y++;
        gbl.setConstraints(filler, gbc);

        revalidate();
        repaint();

    }
}
