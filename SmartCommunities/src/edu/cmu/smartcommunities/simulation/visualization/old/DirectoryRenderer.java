package edu.cmu.smartcommunities.simulation.visualization.old;

// import com.sun.java.swing.*;
// import com.sun.java.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DirectoryRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        if ( value != null && value instanceof Icon ) {
           super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
           setIcon( (Icon)value );
           setText( "" );
           return this;
        }
        else {
           setIcon( null );
        }

        return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    }
}

