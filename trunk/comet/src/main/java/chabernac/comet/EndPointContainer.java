package chabernac.comet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class EndPointContainer {
  private int myEndPointsPerId = 1;
  
  private Map<String, ArrayBlockingQueue<EndPoint>> myEndPoints = new HashMap<String, ArrayBlockingQueue<EndPoint>>();

  public void addEndPoint(EndPoint anEndPoint) throws InterruptedException{
    BlockingQueue<EndPoint> theEndPointQueue = getBlockingQueueFor(anEndPoint.getId());
    //TODO we can not determine at the moment if an endpoint is still valid
    //so we clear all endpoints and store only the last one, which is most likely the one to be valid
    while(theEndPointQueue.size() > myEndPointsPerId - 1){
      theEndPointQueue.poll().destroy();
    }
    theEndPointQueue.put(anEndPoint);
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
  
  public boolean containsEndPointFor(String anId){
    return getNrOfEndPoints( anId ) > 0;
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

  public int getEndPointsPerId() {
    return myEndPointsPerId;
  }

  public void setEndPointsPerId(int anEndPointsPerId) {
    myEndPointsPerId = anEndPointsPerId;
  }
  
  
}
