package chabernac.android.drinklist;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class OrderDrinkActivity extends Activity implements OnClickListener {
  private final int MIN_X = 20;

  private Button next;
  private Button previous;
  private TabHost myTabs;
  private DrinkList myDrinkList = new DrinkList();
  private float myX = 0;
  private float myY = 0;
  private int myLastViewId = -1;

  private Drink myDrink;



  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);




    buildButtons();
    buildTabs();

    //    GridView theGridView = (GridView)findViewById( R.id.colddrinksgrid );
    //    theGridView.setAdapter( new DrinksAdapter( this, "colddrinks", myDrinkList ) );
    //
    //    GridView theGridView2 = (GridView)findViewById( R.id.hotdrinksgrid );
    //    theGridView2.setAdapter( new DrinksAdapter( this, "hotdrinks", myDrinkList ) );
    //
    //    GridView theGridView3 = (GridView)findViewById( R.id.alcoholicdrinksgrid);
    //    theGridView3.setAdapter( new DrinksAdapter( this, "alcoholicdrinks", myDrinkList ) );
    //
    //    GridView theGridView4 = (GridView)findViewById( R.id.ordereddrinks);
    //    theGridView4.setAdapter( new OrderedDrinksAdapter( this, myDrinkList ) );
    //
    //    GridView theGridView5 = (GridView)findViewById( R.id.bierkesgrid);
    //    theGridView5.setAdapter( new DrinksAdapter( this, "bierkes", myDrinkList ) );

    setTitle("Drink List");
  }

  private void buildButtons(){
    ((Button) findViewById(R.id.ListButton)).setOnClickListener( new HomeListener() );
    ((Button) findViewById(R.id.SendButton)).setOnClickListener( new SendListener() );
    ((Button) findViewById(R.id.ClearButton)).setOnClickListener( new ClearListener() );
  }

  private void buildTabs(){
    myTabs = (TabHost) findViewById(R.id.tabhost);
    myTabs.setup();

    Field[] theFields = R.drawable.class.getFields();

    Set<String> theDrinkTypes = new TreeSet<String>();

    for(Field theField : theFields){
      String theFieldName = theField.getName();
      if(theFieldName.contains( "_" )){
        theDrinkTypes.add(theFieldName.substring( 0, theFieldName.indexOf( '_' ) ));
      }
    }

    for(String theDrinkType : theDrinkTypes){
      TabSpec theSpec = myTabs.newTabSpec( theDrinkType );
      theSpec.setIndicator( theDrinkType, locateDrinkTypeIcon( theDrinkType ));
      theSpec.setContent( new DrinkContentFactory(theDrinkType) );  
      myTabs.addTab(theSpec);
    }

    //    myTabs.addTab(myTabs.newTabSpec( "Cold drinks" ).setIndicator( "cold" ));
    //    myTabs.addTab(myTabs.newTabSpec( "Hot drinks" ).setIndicator( "hot" ));
  }

  private Drawable locateDrinkTypeIcon(String aDrinkType){
    String theIconName = aDrinkType + "_icon";
    try {
      Field theField = R.drawable.class.getField( theIconName );
      int theRValue = ((Integer)theField.get( R.drawable.class )).intValue();
      return getResources().getDrawable( theRValue );
    } catch ( Exception e){
      return null;
    }
  }

  public void setCurrentDrink(Drink aDrink){
    myDrink = aDrink;
  }

  public boolean onContextItemSelected(MenuItem item){
    myDrinkList.addDrink( new DrinkOrder( myDrink, item.toString() ) );
    return true;
  }

  @Override
  public void onClick(View v) {
    //    // TODO Auto-generated method stub
    //    if (v == next) {
    //      myViewFlipper.showNext();
    //    }
    //    if (v == previous) {
    //      myViewFlipper.showPrevious();
    //    }
  }

  private void testHorizontalSwipe(MotionEvent anEvent){
    //    float theXDif = anEvent.getX() - myX;
    //    float theYDif = anEvent.getY() - myY;
    //
    //    if(Math.abs(theXDif) >= MIN_X && Math.abs(theXDif) > 2 * Math.abs(theYDif)){
    //      if(theXDif > 0){
    //        myViewFlipper.showNext();
    //        if(myViewFlipper.getCurrentView().getId() == R.id.OverviewPanel ) myViewFlipper.showNext();
    //      } else {
    //        myViewFlipper.showPrevious();
    //        if(myViewFlipper.getCurrentView().getId() == R.id.OverviewPanel ) myViewFlipper.showPrevious();
    //      }
    //      myLastViewId = myViewFlipper.getDisplayedChild();
    //    }
  }

  private void testVerticalSwipe(MotionEvent anEvent){
    float theXDif = anEvent.getX() - myX;
    float theYDif = anEvent.getY() - myY;

    //    if(Math.abs(theYDif) >= MIN_X && Math.abs(theYDif) > 2 * Math.abs(theXDif)){
    //      if(myViewFlipper.getCurrentView().getId() == R.id.OverviewPanel){
    //        myViewFlipper.setDisplayedChild(myLastViewId);
    //      } else {
    //        myViewFlipper.setDisplayedChild( 0 );
    //      }
    //    }
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
    return super.dispatchTouchEvent(anEvent);
  }

  private class HomeListener implements OnClickListener {
    @Override
    public void onClick( View aView ) {
      //      myViewFlipper.setDisplayedChild( R.id.OverviewPanel );
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

  private class ClearListener implements OnClickListener {
    @Override
    public void onClick( View aView ) {
      myDrinkList.clear();
    }
  }

  public class DrinkContentFactory implements TabContentFactory {
    private final String myDrinkType;

    public DrinkContentFactory(String aDrinkType){
      myDrinkType = aDrinkType;
    }

    @Override
    public View createTabContent( String aTag ) {
      GridView theGrid = new GridView( OrderDrinkActivity.this );
      theGrid.setNumColumns( 4 );
      theGrid.setAdapter( new DrinksAdapter( OrderDrinkActivity.this, myDrinkType, myDrinkList ) );
      return theGrid;
    }

  }
}