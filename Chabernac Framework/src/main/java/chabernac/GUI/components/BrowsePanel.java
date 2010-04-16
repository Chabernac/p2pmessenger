package chabernac.GUI.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import chabernac.GUI.utils.*;
import chabernac.file.*;
import java.io.File;

public class BrowsePanel extends JPanel{
	private ExtensionFileFilter myExtensionFileFilter = null;
	private JButton myBrowseButton = null;
	private JTextField myFile = null;
	private JLabel myFileDescription = null;
	private GridBagLayout layout = null;
	private String myDescription = null;


	public BrowsePanel(){
		this("");
	}

	public BrowsePanel(String aDescription){
		this(aDescription, null);
	}

	public BrowsePanel(String aDescription, ExtensionFileFilter aFileFilter){
		myExtensionFileFilter = aFileFilter;
		myDescription = aDescription;
		initialize();
		addListeners();
		setupGui();
	}

	private void initialize(){
		myFile = new JTextField();
		myBrowseButton = new JButton("Browse");
		myFileDescription = new JLabel(myDescription);
		layout = new GridBagLayout();
	}

	private void addListeners(){
		ButtonListener myButtonListener = new ButtonListener();
		myBrowseButton.addActionListener(myButtonListener);
	}

	private void setupGui(){
		setLayout(layout);
		Insets insets = new Insets(1,1,1,1);
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,1,1,1,1,myFileDescription,Color.black);
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,1,0,2,1,1,1,myFile,Color.black);
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,3,1,1,1,myBrowseButton,Color.black);
	}

	private class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent evt){
			if(evt.getActionCommand().equals(myBrowseButton.getActionCommand())){
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle(myDescription);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(false);
				if(myExtensionFileFilter != null){
					fileChooser.setFileFilter(myExtensionFileFilter);
				}
				if(!myFile.getText().equals("")){fileChooser.setSelectedFile(new File(myFile.getText()));}
				int returnVal = fileChooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION){
					myFile.setText(fileChooser.getSelectedFile().toString());
				}

			}
		}
	}

	public void setFile(String aFile){
		myFile.setText(aFile);
	}

	public String getFile(){
		return myFile.getText();
	}
}