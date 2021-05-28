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

    public void configure() {
        this.setBorder(null);
        JList jList = new JList();
        DefaultListModel <String> model = new DefaultListModel<>();
        jList.setModel(model);
        jList.setCellRenderer(new MyListCellRenderer());
        this.setViewportView(jList);
    }
}
