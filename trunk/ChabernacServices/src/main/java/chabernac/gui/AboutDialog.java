package chabernac.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

import chabernac.command.AbstractCommand;

public class AboutDialog extends JDialog {
  private static final long serialVersionUID = 2056174453737153731L;

  public AboutDialog(Frame owner, String aTitle, JPanel aContent){
    super(owner);
    setModal(true);
    setTitle(aTitle);
    buildGUI(aContent);
  }
  
  private void buildGUI(JPanel aContent){
    setLocationRelativeTo(getParent());
    getContentPane().setLayout(new BorderLayout());
    JPanel thePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    thePanel.add(new CommandButton(new OkCommand()));
    getContentPane().add(thePanel, BorderLayout.SOUTH);
    getContentPane().add(aContent, BorderLayout.CENTER);
    pack();
  }
  
  private class OkCommand extends AbstractCommand{

    public String getName() {
      return "Ok";
    }

    public boolean isEnabled() {
      return true;
    }

    public void execute() {
      setVisible(false);
    }
    
  }

}
