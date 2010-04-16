package chabernac.command;

public abstract class ExitCommand implements Command{
	public void execute(){
		exit();
	}

	protected abstract void exit();
}