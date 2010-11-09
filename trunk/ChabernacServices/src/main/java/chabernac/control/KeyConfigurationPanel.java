
package chabernac.control;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import chabernac.gui.utils.GUIUtils;

public class KeyConfigurationPanel extends JPanel {
  
  private KeyMapContainer myKeyMappings;
  private FocusListener myFocusListener; 
  private GridBagLayout layout = null;
  private static String NOT_ASSIGNED = "Not assigned";
  public static Color SELECTED = new Color(250,250,210);
  public static Color NOT_SELECTED = new Color(220,220,220);
  
  
  public KeyConfigurationPanel(KeyMapContainer aKeyMappings){
    super();
    myKeyMappings = aKeyMappings;
    initialize();
    buildGUI();
  }
  
  private void initialize(){
    myFocusListener = new MyFocusListener();
    layout = new GridBagLayout();
  }
  
  
  
  private void buildGUI(){
    setLayout(layout);
    Insets insets = new Insets(2,2,2,2);
    GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.BOTH,1,1,0,0,1,1,buildConfigurationPanel(),Color.black);
    setBorder(new EtchedBorder());
    
  }
  
  public JPanel buildConfigurationPanel(){
    GridBagLayout theLayout = new GridBagLayout();
    Insets theDescriptionInsets = new Insets(2,2,2,20); 
    Insets theKeyInsets = new Insets(2,2,2,2);
    JPanel thePanel = new JPanel(theLayout);
    JLabel theDescription = null;
    JTextField theKey = null;
    for(int i=0;i<myKeyMappings.size();i++){
      theDescription = new JLabel(myKeyMappings.keyMapAt(i).getCommand().getDescription());
      GUIUtils.addMyComponent(thePanel,theLayout,GridBagConstraints.WEST,theDescriptionInsets,GridBagConstraints.NONE,0,0,0,i,1,1,theDescription,Color.black);
      for(int j=0;j<myKeyMappings.keyMapAt(i).getKeyCodes().length;j++){
        theKey = new JTextField(getKeyText(myKeyMappings.keyMapAt(i).getKeyCodes()[j]));
        theKey.setBorder(new EtchedBorder());
        theKey.setHorizontalAlignment(SwingConstants.CENTER);
        theKey.setEditable(false);
        theKey.setColumns(NOT_ASSIGNED.length());
        theKey.setBackground(NOT_SELECTED);
        GUIUtils.addMyComponent(thePanel,theLayout,GridBagConstraints.WEST,theKeyInsets,GridBagConstraints.HORIZONTAL,1,0,j + 1,i,1,1,theKey,Color.black);
        theKey.addKeyListener(new KeyMapListener(myKeyMappings.keyMapAt(i),j));
        theKey.addFocusListener(myFocusListener);
                
      }
      
    }
    return thePanel;
  }
  
  private String getKeyText(int aKeyCode){
    if(aKeyCode == 0) return NOT_ASSIGNED;
    else return KeyEvent.getKeyText(aKeyCode);
  }
  
  private class KeyMapListener implements KeyListener{
    private KeyMap myKeyMap = null;
    private int myKey;
    
    public KeyMapListener(KeyMap aKeyMap, int whichKey){
      myKeyMap = aKeyMap;
      myKey = whichKey;
    }
    public void keyPressed(KeyEvent e){
      if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
        myKeyMap.getKeyCodes()[myKey] = 0;  
      } else {
        myKeyMap.getKeyCodes()[myKey] = e.getKeyCode();
      }
      ((JTextField)e.getSource()).setText(getKeyText(myKeyMap.getKeyCodes()[myKey]));
    }
    public void keyReleased(KeyEvent e){  }
    public void keyTyped(KeyEvent e) { }
  }
  
  private class MyFocusListener implements FocusListener{
   
    public void focusGained(FocusEvent evt) {
      ((JComponent)evt.getSource()).setBackground(SELECTED);
    }
    
    public void focusLost(FocusEvent evt) {
      ((JComponent)evt.getSource()).setBackground(NOT_SELECTED);
    }
  }

}
