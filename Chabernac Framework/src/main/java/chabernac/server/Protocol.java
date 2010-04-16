package chabernac.server;

import java.io.*;

public interface Protocol
{
  public void handle(InputStream input, OutputStream output) throws Exception;
}