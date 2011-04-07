/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.search;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchPanel extends JPanel {
  private static final long serialVersionUID = -4979535088126867807L;

  private final AbstractSearchProvider mySearchProvider;

  private JTextField mySearchArea = new JTextField();
  private JCheckBox myRegularExpression = new JCheckBox("Regular expression");

  public SearchPanel(AbstractSearchProvider aProvider){
    mySearchProvider = aProvider;
    buildGUI();
  }

  private void buildGUI(){
    setLayout( new GridBagLayout() );

    GridBagConstraints theCons = new GridBagConstraints();
    theCons.gridx = 0;
    theCons.gridy = 0;
    theCons.insets = new Insets( 2, 2, 2, 2 );
    
    add(new JLabel( "Search what:"), theCons);
    
    theCons.gridx += 1;
    theCons.weightx = 1;
    theCons.weighty = 0;
    theCons.fill = GridBagConstraints.HORIZONTAL;
    add(mySearchArea, theCons);

    theCons.gridx += 1;
    theCons.weightx = 0;
    theCons.fill = GridBagConstraints.NONE;
    theCons.anchor = GridBagConstraints.EAST;

    add(buildButton( new NextAction() ), theCons);

    theCons.gridy += 1;

    add(buildButton( new PreviousAction() ), theCons);
    
    theCons.gridx = 0;
    theCons.gridy = 1;
    theCons.anchor = GridBagConstraints.WEST;
    theCons.weightx = 0;
    theCons.gridwidth = 2;
    
    add(myRegularExpression, theCons);
  }
  
  public void requestFocus(){
    super.requestFocus();
    mySearchArea.requestFocus();
  }
  
  private JButton buildButton(AbstractAction anAction){
    JButton theButton = new JButton(anAction);
    theButton.setPreferredSize( new Dimension(100, getPreferredSize().height) );
    return theButton;
  }

  public class NextAction extends AbstractAction{
    public NextAction(){
      putValue( Action.NAME, "Next" );
    }

    @Override
    public void actionPerformed( ActionEvent aE ) {
      try {
        mySearchProvider.next(mySearchArea.getText().trim(), false);
      } catch ( SearchProviderException e ) {
      } 
    }
  }

  public class PreviousAction extends AbstractAction{
    public PreviousAction(){
      putValue( Action.NAME, "Previous" );
    }

    @Override
    public void actionPerformed( ActionEvent aE ) {
      try {
        mySearchProvider.previous(mySearchArea.getText().trim(), myRegularExpression.isSelected());
      } catch ( SearchProviderException e ) {
      }
    }
  }
}
