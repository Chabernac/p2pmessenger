package chabernac.p2pclient.gui.action;


import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.gui.ApplicationLauncher;
import chabernac.protocol.facade.P2PFacadeException;

public class ShowFrameCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(ShowFrameCommand.class);

	@Override
	public void execute() {
		try {
			ApplicationLauncher.showChatFrame();
		} catch ( P2PFacadeException e ) {
			LOGGER.error("Unable to load chat frame", e);
		}
	}

}
