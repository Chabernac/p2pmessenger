package chabernac.comet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Endpoint;

public class EndPointContainer {
  private Map<String, ArrayBlockingQueue<Endpoint>> myEndPoints = new HashMap<String, ArrayBlockingQueue<Endpoint>>();

  public void addEndPoint(EndPoint anEndPoint) throws InterruptedException{
    getBlockingQueueFor(anEndPoint.getId()).put(anEndPoint);
  }

  public Endpoint getEndPointFor(String anId, int aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
   return getBlockingQueueFor(anId).poll(aTimeout, aTimeUnit);
  }
  
  private ArrayBlockingQueue<EndPoint> getBlockingQueueFor(String anId){
    if(!myEndPoints.containsKey(anId)){
      myEndPoints.put(anId, new ArrayBlockingQueue<Endpoint>(8));
    }
    return myEndPoints.get(anId);
  }

  public int getNrOfEndPoints(String anId){
    if(!myEndPoints.containsKey(anId)) return 0;
    return myEndPoints.get(anId).size();
  }
}
