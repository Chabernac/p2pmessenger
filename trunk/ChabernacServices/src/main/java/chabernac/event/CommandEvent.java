package chabernac.event;

public class CommandEvent extends Event{
  private static final long serialVersionUID = -7345165559071572036L;
  public String myCommand = null;

  public CommandEvent(String aCommand) {
    super("Command event");
    myCommand = aCommand;
  }

  public String getCommand() {
    return myCommand;
  }

  public void setCommand(String anCommand) {
    myCommand = anCommand;
  }
}
