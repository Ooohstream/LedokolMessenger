package ledokolmessenger.client.ui;

import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import ledokolmessenger.client.utillities.WordWrapCellRenderer;

/**
 *
 * @author OMEN
 */
public class MessagesTable extends JTable {

    public void configure() {
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setBorder(BorderFactory.createBevelBorder(192));
        this.setFont(new Font("Verdana", 0, 14));

        this.setFocusable(false);

        this.setGridColor(new java.awt.Color(255, 255, 255));

        this.setSelectionBackground(new java.awt.Color(255, 255, 255));

        this.setShowHorizontalLines(false);

        this.setShowVerticalLines(false);

        this.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"", "", "", ""}
        ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        this.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
        this.getColumnModel().getColumn(2).setCellRenderer(new WordWrapCellRenderer());

        this.getTableHeader().setResizingAllowed(false);
        this.getTableHeader().setReorderingAllowed(false);
        this.setRowHeight(50);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        if(column == 0 || column == 3)
        {
        int rendererWidth = component.getPreferredSize().width;
        TableColumn tableColumn = getColumnModel().getColumn(column);
        tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
        }
        return component; 
    }

}
