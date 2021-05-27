package ledokolmessenger.client.ui;

import javax.swing.JScrollPane;

/**
 *
 * @author OMEN
 */
public class MessagesScrollPane extends JScrollPane {

    public void configure() {
        this.setBorder(null);
        MessagesTable messagesTable = new MessagesTable();
        messagesTable.configure();
        this.setViewportView(messagesTable);
    }
}
