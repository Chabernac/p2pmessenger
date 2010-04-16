package chabernac.command;

public interface ButtonCommand extends Command{
  public String getName();
  public boolean isEnabled();
  public char getMnemonic();
}
