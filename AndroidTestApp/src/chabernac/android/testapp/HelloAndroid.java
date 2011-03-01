package chabernac.android.testapp;

import android.app.Activity;
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
  private float theX1;
  private float theY1;
  private DrinkList myDrinkList = new DrinkList();
  
  

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    myViewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
    
    ((Button) findViewById(R.id.ListButton)).setOnClickListener( new HomeListener() );

    GridView theGridView = (GridView)findViewById( R.id.colddrinks );
    theGridView.setAdapter( new DrinksAdapter( this, "colddrinks", myDrinkList ) );

    GridView theGridView2 = (GridView)findViewById( R.id.hotdrinks );
    theGridView2.setAdapter( new DrinksAdapter( this, "hotdrinks", myDrinkList ) );
    
    GridView theGridView3 = (GridView)findViewById( R.id.alcoholicdrinks);
    theGridView3.setAdapter( new DrinksAdapter( this, "alcoholicdrinks", myDrinkList ) );
    
    GridView theGridView4 = (GridView)findViewById( R.id.ordereddrinks);
    theGridView4.setAdapter( new OrderedDrinksAdapter( this, myDrinkList ) );

    setTitle("Buttler");
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
  @Override
  public boolean dispatchTouchEvent(MotionEvent anEvent) {
    // TODO Auto-generated method stub
    if(anEvent.getAction() == MotionEvent.ACTION_DOWN){
      theX1 = anEvent.getX();
      theY1 = anEvent.getY();
    } else if(anEvent.getAction() == MotionEvent.ACTION_UP){
      float theXDif = anEvent.getX() - theX1;
      float theYDif = anEvent.getY() - theY1;
      
      if(Math.abs(theXDif) >= MIN_X && Math.abs(theXDif) > 2 * Math.abs(theYDif)){
        if(theXDif > 0){
          myViewFlipper.showNext();
        } else {
          myViewFlipper.showPrevious();
        }
      }
    }
    System.out.println(anEvent);
    return super.dispatchTouchEvent(anEvent);
  }

  public class HomeListener implements OnClickListener {
    @Override
    public void onClick( View aView ) {
      myViewFlipper.setDisplayedChild( R.id.OverviewPanel );
    }
  }
}