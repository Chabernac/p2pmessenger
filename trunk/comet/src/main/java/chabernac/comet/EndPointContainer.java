package chabernac.comet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class EndPointContainer {
  private Map<String, ArrayBlockingQueue<EndPoint>> myEndPoints = new HashMap<String, ArrayBlockingQueue<EndPoint>>();

  public void addEndPoint(EndPoint anEndPoint) throws InterruptedException{
    getBlockingQueueFor(anEndPoint.getId()).put(anEndPoint);
  }

  public EndPoint getEndPointFor(String anId, int aTimeout, TimeUnit aTimeUnit) throws InterruptedException{
   return getBlockingQueueFor(anId).poll(aTimeout, aTimeUnit);
  }
  
  private BlockingQueue<EndPoint> getBlockingQueueFor(String anId){
    if(!myEndPoints.containsKey(anId)){
      myEndPoints.put(anId, new ArrayBlockingQueue<EndPoint>(8));
    }
    return myEndPoints.get(anId);
  }

  public int getNrOfEndPoints(String anId){
    if(!myEndPoints.containsKey(anId)) return 0;
    return myEndPoints.get(anId).size();
  }
  
  public int size(){
    return myEndPoints.size();
  }
  
  public List<EndPoint> getAllEndPoints(){
    List<EndPoint> theEndPoints = new ArrayList<EndPoint>();
    for(BlockingQueue<EndPoint> theQueue : myEndPoints.values()){
      for(EndPoint theEndPoint : theQueue){
        theEndPoints.add( theEndPoint );
      }
    }
    return theEndPoints;
  }
}
