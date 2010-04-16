package chabernac.queue;

import chabernac.server.DefaultObjectProtocol;
import chabernac.utils.Debug;

public class QueueProtocol extends DefaultObjectProtocol
{
  private Queue queue = null;

  public QueueProtocol(Queue queue)
  {
	super();
    this.queue = queue;
  }


  public void handle()
  {

    SocketPusher pusher = null;
    try
    {
	  send("MESSAGE");
      send("HELLO");
	  String cmd = "";
	  while(!(cmd = ((String)receive()).toUpperCase()).equals("QUIT"))
	  {
		  try
		  {
			 if(cmd.equals("GET"))
			 {
				 if(queue.isEmpty())
				 {
					 send("MESSAGE");
					 send("QUEUE EMPTY");
				 }
				 else if(!queue.isGetEnabled())
				 {
					 send("MESSAGE");
					 send("GET DISABLED");
				 }
				 else
				 {
					 send("PUT");
					 send(queue.get());
				 }
			 }
			 else if(cmd.equals("PUT"))
			 {
				 if(!queue.isPutEnabled())
				 {
					 send("MESSAGE");
					 send("PUT DISABLED");
				 }
				 else{queue.put(receive());}
			 }
			 else if(cmd.equals("PUSH"))
			 {
				 if(pusher==null)
				 {
					pusher = new SocketPusher(queue, this);
					queue.trigger();
				 }
			 }
			 else if(cmd.equals("UNPUSH"))
			 {
				 if(pusher!=null)
				 {
					 try
				        {
				 		 queue.deleteObserver(pusher);
				 		 pusher = null;
       					}catch(Exception e){System.out.println("Error: " + e);}
				 }
			 }
			 else if(cmd.equals("FLOOD"))
			 {
				 for(int i=0;i<200;i++)
				 {
					 queue.put("message " + i);
				 }
			 }
			 else if(cmd.equals("MESSAGE"))
			 {
				 Debug.log(this,(String)receive());
			 }
			 else
			 {
				 Debug.log(this,"Unknown command received: " + cmd);
			 }
		 }catch(Exception e){send("Error: " + e);}
	  }

    }catch(Exception e){System.out.println("Error: " + e);}
     finally
     {
       try
       {
		 queue.deleteObserver(pusher);
       }catch(Exception e){System.out.println("Error: " + e);}
     }
  }
}
