package chabernac.plugin.exception;

public class PluginNotShutDownException extends Exception {

	private static final long serialVersionUID = -2533761391254982101L;

	public PluginNotShutDownException() {
		super();
	}

	public PluginNotShutDownException(String anArg0, Throwable anArg1) {
		super(anArg0, anArg1);
	}

	public PluginNotShutDownException(String anArg0) {
		super(anArg0);
	}

	public PluginNotShutDownException(Throwable anArg0) {
		super(anArg0);
	}

}
