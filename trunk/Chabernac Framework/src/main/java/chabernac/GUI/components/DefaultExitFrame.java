package chabernac.GUI.components;

import chabernac.command.*;

public class DefaultExitFrame extends ExitFrame{
	public DefaultExitFrame(){
		super(new DefaultExitCommand());
	}
}