package chabernac.plugin.exception;

public class PluginNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9067197780618236439L;

	public PluginNotFoundException() {
		super();
	}

	public PluginNotFoundException(String anMessage, Throwable anCause) {
		super(anMessage, anCause);
	}

	public PluginNotFoundException(String anMessage) {
		super(anMessage);
	}

	public PluginNotFoundException(Throwable anCause) {
		super(anCause);
	}

}
