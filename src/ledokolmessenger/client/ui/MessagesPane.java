package ledokolmessenger.client.ui;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import ledokolmessenger.client.utillities.MyListCellRenderer;

/**
 *
 * @author OMEN
 */
public class MessagesPane extends JScrollPane {

    private DefaultListModel<String> model;
    private JList messageList;
    
    public void configure() {
        this.setBorder(null);
        this.messageList = new JList();
        this.model = new DefaultListModel<>();
        messageList.setModel(model);
        //messageList.setCellRenderer(new MyListCellRenderer());
        this.setViewportView(messageList);
    }

    public DefaultListModel<String> getModel() {
        return model;
    }

    public JList getMessageList() {
        return messageList;
    }
    
    
}
