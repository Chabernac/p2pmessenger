package chabernac.utils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import chabernac.GUI.utils.GUIUtils;

public class ProxyPanel extends JPanel
{
	private JCheckBox useProxy = null;
	//private JLabel useProxyL = null;
	private JLabel proxyHostL = null;
	private JLabel proxyPortL = null;
	private JTextField proxyHost = null;
	private JTextField proxyPort = null;
	GridBagLayout layout = null;


	public ProxyPanel()
	{
		initialize();
		addListeners();
		setupGui();
		checkState();
	}

private void initialize()
{
	useProxy = new JCheckBox("Use proxy");
	//useProxyL = new JLabel("Use proxy");
	proxyHostL = new JLabel("Proxy host");
	proxyPortL = new JLabel("Proxy port");
	proxyHost = new JTextField();
	proxyPort = new JTextField();
	layout = new GridBagLayout();
}


private void addListeners()
{
	useProxy.addItemListener(new ItemAdaptor());
}

private void setupGui()
{
	setLayout(layout);
	Insets insets = new Insets(2,2,2,2);

	//GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,1,1,1,1,useProxyL,Color.black);
	GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,1,2,1,1,proxyHostL,Color.black);
	GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,1,3,1,1,proxyPortL,Color.black);
	GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,1,0,1,1,2,1,useProxy,Color.black);
	GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,1,0,2,2,1,1,proxyHost,Color.red);
	GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,1,0,2,3,1,1,proxyPort,Color.red);
	//setBorder(new TitledBorder("Proxy settings"));
}


private void checkState()
{
	if(useProxy.isSelected())
	{
		proxyHostL.setVisible(true);
		proxyPortL.setVisible(true);
		proxyHost.setVisible(true);
		proxyPort.setVisible(true);
	}
	else
	{
		proxyHostL.setVisible(false);
		proxyPortL.setVisible(false);
		proxyHost.setVisible(false);
		proxyPort.setVisible(false);
	}

	revalidate();
}

private void setProxySettings(ProxySettings settings)
{
	useProxy.setSelected(settings.useProxy());
	proxyHost.setText(settings.getProxyHost());
	proxyPort.setText(Integer.toString(settings.getProxyPort()));
	checkState();
}

public ProxySettings getSettings()
{
	ProxySettings settings = new ProxySettings();
	settings.setProxy(useProxy.isSelected());
	settings.setProxyHost(proxyHost.getText());
	settings.setProxyPort(Integer.parseInt(proxyPort.getText()));
	return settings;
}

private class ItemAdaptor implements ItemListener
{
public void itemStateChanged(ItemEvent e)
	{
		checkState();
	}
}

}
