package chabernac.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GUIUtils {
  public static void addComponent(Container aContainer, Component aComponent, int gridx, int gridy, int weightx, int weighty, int fill, Insets inset){
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.gridx = gridx;
    theCons.gridy = gridy;
    theCons.weightx = weightx;
    theCons.weighty = weighty;
    theCons.fill = fill;
    theCons.insets = inset;
    theCons.anchor = GridBagConstraints.WEST;
    aContainer.add(aComponent, theCons);
  }
  
  public static void addComponent(Container aContainer, Component aComponent, int gridx, int gridy, int aGridWith, int aGridHeight, int weightx, int weighty, int fill, Insets inset){
    GridBagConstraints theCons = new GridBagConstraints();
    theCons.gridx = gridx;
    theCons.gridy = gridy;
    theCons.weightx = weightx;
    theCons.weighty = weighty;
    theCons.fill = fill;
    theCons.insets = inset;
    theCons.anchor = GridBagConstraints.WEST;
    theCons.gridwidth = aGridWith;
    theCons.gridheight = aGridHeight;
    aContainer.add(aComponent, theCons);
  }
}
