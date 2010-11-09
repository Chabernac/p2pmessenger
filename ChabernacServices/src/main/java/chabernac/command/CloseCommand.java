package chabernac.command;

public abstract class CloseCommand implements Command{
	public void execute(){
		close();
	}

	protected abstract void close();
}