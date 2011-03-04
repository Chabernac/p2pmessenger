package chabernac.android.testapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ViewFlipper;

public class HelloAndroid extends Activity implements OnClickListener {
  private final int MIN_X = 20;

  private Button next;
  private Button previous;
  private ViewFlipper myViewFlipper;
  private DrinkList myDrinkList = new DrinkList();
  private float myX = 0;
  private float myY = 0;
  
  

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    myViewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
    
    ((Button) findViewById(R.id.ListButton)).setOnClickListener( new HomeListener() );
    ((Button) findViewById(R.id.SendButton)).setOnClickListener( new SendListener() );

    GridView theGridView = (GridView)findViewById( R.id.colddrinks );
    theGridView.setAdapter( new DrinksAdapter( this, "colddrinks", myDrinkList ) );

    GridView theGridView2 = (GridView)findViewById( R.id.hotdrinks );
    theGridView2.setAdapter( new DrinksAdapter( this, "hotdrinks", myDrinkList ) );
    
    GridView theGridView3 = (GridView)findViewById( R.id.alcoholicdrinks);
    theGridView3.setAdapter( new DrinksAdapter( this, "alcoholicdrinks", myDrinkList ) );
    
    GridView theGridView4 = (GridView)findViewById( R.id.ordereddrinks);
    theGridView4.setAdapter( new OrderedDrinksAdapter( this, myDrinkList ) );

    setTitle("Buttler");
    MailReceiver theReceiver = new MailReceiver();
    
    IntentFilter theFilter = new IntentFilter(Intent.ACTION_VIEW);
    registerReceiver(theReceiver , theFilter );
    
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if (v == next) {
      myViewFlipper.showNext();
    }
    if (v == previous) {
      myViewFlipper.showPrevious();
    }
  }
  
  private void testHorizontalSwipe(MotionEvent anEvent){
    float theXDif = anEvent.getX() - myX;
    float theYDif = anEvent.getY() - myY;
    
    if(Math.abs(theXDif) >= MIN_X && Math.abs(theXDif) > 2 * Math.abs(theYDif)){
      if(theXDif > 0){
        myViewFlipper.showNext();
        if(myViewFlipper.getCurrentView().getId() == R.id.OverviewPanel ) myViewFlipper.showNext();
      } else {
        myViewFlipper.showPrevious();
        if(myViewFlipper.getCurrentView().getId() == R.id.OverviewPanel ) myViewFlipper.showPrevious();
      }
    }
  }
  
  private void testVerticalSwipe(MotionEvent anEvent){
    float theXDif = anEvent.getX() - myX;
    float theYDif = anEvent.getY() - myY;
    
    if(Math.abs(theYDif) >= MIN_X && Math.abs(theYDif) > 2 * Math.abs(theXDif)){
      myViewFlipper.setDisplayedChild( R.id.OverviewPanel );
    }
  }
  
  @Override
  public boolean dispatchTouchEvent(MotionEvent anEvent) {
    // TODO Auto-generated method stub
    if(anEvent.getAction() == MotionEvent.ACTION_DOWN){
      myX = anEvent.getX();
      myY = anEvent.getY();
    } else if(anEvent.getAction() == MotionEvent.ACTION_UP){
      testHorizontalSwipe( anEvent );
      testVerticalSwipe( anEvent );
    }
    System.out.println(anEvent);
    return super.dispatchTouchEvent(anEvent);
  }

  private class HomeListener implements OnClickListener {
    @Override
    public void onClick( View aView ) {
      myViewFlipper.setDisplayedChild( R.id.OverviewPanel );
    }
  }
  
  private class SendListener implements OnClickListener {
    @Override
    public void onClick( View aView ) {
      Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
      emailIntent.setType("plain/text");
      emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,  new String[]{"guy.chauliac@gmail.com"});
      emailIntent.putExtra(android.content.Intent.EXTRA_PHONE_NUMBER,  new String[]{"0032486331565"});
//      emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "drink order");
      emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, myDrinkList.toString());
      startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
  }
}