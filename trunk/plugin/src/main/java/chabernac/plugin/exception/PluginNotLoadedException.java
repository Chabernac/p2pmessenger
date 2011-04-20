package chabernac.plugin.exception;

public class PluginNotLoadedException extends Exception {

	private static final long serialVersionUID = -7808887975931193869L;

	public PluginNotLoadedException() {
		super();
	}

	public PluginNotLoadedException(String anArg0, Throwable anArg1) {
		super(anArg0, anArg1);
	}

	public PluginNotLoadedException(String anArg0) {
		super(anArg0);
	}

	public PluginNotLoadedException(Throwable anArg0) {
		super(anArg0);
	}

}
