package edu.cmu.smartcommunities.simulation.visualization.old;

import com.sun.java.swing.*;
// import com.sun.java.swing.event.*;
// import com.sun.java.swing.table.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class FileExplorer {
    public static void main( String[] argv ) {
        JFrame frame = new JFrame( "File Explorer" );

        frame.addWindowListener( new WindowAdapter() {
                                     public void windowClosing( WindowEvent e ) {
                                         System.exit( 0 );
                                     }
                                 });

        FileSystemModel model = new FileSystemModel();
        DirectoryModel directoryModel = new DirectoryModel( (File)model.getRoot() );
        JTable table = new JTable( directoryModel );
        table.setShowHorizontalLines( false );
        table.setShowVerticalLines( false );
        table.setIntercellSpacing( new Dimension( 0, 2 ) );
        table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        table.getColumn( "Type" ).setCellRenderer( new DirectoryRenderer() );
        table.getColumn( "Type" ).setMaxWidth( 32 );
        table.getColumn( "Type" ).setMinWidth( 32 );

        FileSystemTreePanel fileTree = new FileSystemTreePanel( model );
        fileTree.getTree().addTreeSelectionListener( new TreeListener( directoryModel ) );

        JScrollPane treeScroller = new JScrollPane( fileTree );
        JScrollPane tableScroller = JTable.createScrollPaneForTable( table );
        treeScroller.setMinimumSize( new Dimension( 0, 0 ) );
        tableScroller.setMinimumSize( new Dimension( 0, 0 ) );
        tableScroller.setBackground( Color.white );
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                                               treeScroller,
                                               tableScroller );
        splitPane.setContinuousLayout( true );

        frame.getContentPane().add( splitPane );

        frame.setSize( 400, 400 );
        frame.pack();
        frame.show();
    }

    protected static class TreeListener implements TreeSelectionListener {
        DirectoryModel model;

        public TreeListener( DirectoryModel mdl ) {
            model = mdl;
        }
        public void valueChanged( TreeSelectionEvent e ) {
            File fileSysEntity = (File)e.getPath().getLastPathComponent();
            if ( fileSysEntity.isDirectory() ) {
                model.setDirectory( fileSysEntity );
            }
            else {
                model.setDirectory( null );
            }
        }
    }
}
