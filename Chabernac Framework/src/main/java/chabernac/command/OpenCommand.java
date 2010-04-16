package chabernac.command;

public abstract class OpenCommand implements Command{
	public void execute(){
		open();
	}

	protected abstract void open();
}