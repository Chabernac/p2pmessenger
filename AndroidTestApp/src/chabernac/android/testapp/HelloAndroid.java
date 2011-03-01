package chabernac.android.testapp;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ViewFlipper;

public class HelloAndroid extends Activity implements OnClickListener, OnGesturePerformedListener {

  private final int MIN_X = 20;

  Button next;
  Button previous;
  ViewFlipper vf;
  GestureLibrary myLibrary;
  float theX1, theX2;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    vf = (ViewFlipper) findViewById(R.id.ViewFlipper01);
    next = (Button) findViewById(R.id.Button01);
    previous = (Button) findViewById(R.id.Button02);
    next.setOnClickListener(this);
    previous.setOnClickListener(this);

    GridView theGridView = (GridView)findViewById( R.id.colddrinks );
    theGridView.setAdapter( new DrinksAdapter( this, "colddrinks" ) );

    GridView theGridView2 = (GridView)findViewById( R.id.hotdrinks );
    theGridView2.setAdapter( new DrinksAdapter( this, "hotdrinks" ) );
    
    GridView theGridView3 = (GridView)findViewById( R.id.alcoholicdrinks);
    theGridView3.setAdapter( new DrinksAdapter( this, "alcoholicdrinks" ) );

    setTitle("Buttler");
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if (v == next) {
      vf.showNext();
    }
    if (v == previous) {
      vf.showPrevious();
    }
  }

  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    //      System.out.println(gesture.pr)
  }

  @Override
  public boolean onKeyDown(int anKeyCode, KeyEvent anEvent) {
    // TODO Auto-generated method stub
    return super.onKeyDown(anKeyCode, anEvent);
  }

  @Override
  public boolean onKeyLongPress(int anKeyCode, KeyEvent anEvent) {
    // TODO Auto-generated method stub
    return super.onKeyLongPress(anKeyCode, anEvent);
  }


  @Override
  public boolean dispatchTouchEvent(MotionEvent anEvent) {
    // TODO Auto-generated method stub
    if(anEvent.getAction() == MotionEvent.ACTION_DOWN){
      theX1 = anEvent.getX();
    } else if(anEvent.getAction() == MotionEvent.ACTION_UP){
      if(anEvent.getX() - theX1 >= MIN_X ){
        vf.showNext();
      } else if(theX1 - anEvent.getX() >= MIN_X){
        vf.showPrevious(); 
      }
    }
    System.out.println(anEvent);
    return super.dispatchTouchEvent(anEvent);
  }





}