package chabernac.command;

public abstract class SaveCommand implements Command{
	public void execute(){
		save();
	}

	protected abstract void save();
}