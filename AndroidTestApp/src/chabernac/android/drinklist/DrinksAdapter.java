/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DrinksAdapter extends BaseAdapter implements OnClickListener {
  private List<Drink> myDrinks = new ArrayList<Drink>();
  private DrinkList myDrinkList;
  private OrderDrinkActivity myActivity;

  public DrinksAdapter(OrderDrinkActivity anActivity, String aDrinks, DrinkList aDrinkList) {
    myDrinkList = aDrinkList;
    myActivity = anActivity;
    loadImages(aDrinks);
  }

  private void loadImages(String aDrinks){
    Field[] theFields = R.drawable.class.getFields();
    for(Field theField : theFields){
      if(theField.getName().startsWith(aDrinks)){
        try {
          Drink theDrink  = new Drink( aDrinks, theField.getName().substring( aDrinks.length() + 1 ), (Integer)theField.get(R.drawable.class));
          loadOptions(theDrink);
          myDrinks.add( theDrink );
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void loadOptions(Drink aDrink){
    BufferedReader theReader =  null;
    try {
      theReader = new BufferedReader( new InputStreamReader( myActivity.getAssets().open( aDrink.getName() + ".txt" ) ));
      String theLine = null;
      while((theLine = theReader.readLine()) != null){
        aDrink.addDrinkOption( theLine );
      }
    } catch ( IOException e ) {
    } finally {
      try {
        if(theReader != null){
          theReader.close();
        }
      } catch ( IOException e ) {
      }
    }
  }

  public int getCount() {
    return myDrinks.size();
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    DrinkView theDrinkView = getDrinkView( (DrinkView)convertView, position);

    int theNrOfDrinks = myDrinkList.getDrinkOrder( theDrinkView.getDrink() ); 

    if( theNrOfDrinks > 0){
      theDrinkView.setContentDescription( Integer.toString(theNrOfDrinks) );
    }

    return theDrinkView;
  }

  private DrinkView getDrinkView(DrinkView aDrinkView, int aPosition){
    if(aDrinkView != null) return aDrinkView;

    DrinkView theDrinkView = new DrinkView(myActivity, myDrinks.get(aPosition));
    myActivity.registerForContextMenu( theDrinkView );
    return theDrinkView;
  }

  @Override
  public void onClick( View aView ) {
    // TODO Auto-generated method stub

  }

  private class DrinkView extends ImageButton implements OnClickListener{
    private final Drink myDrink;

    public DrinkView( Context aContext, Drink aDrink ) {
      super( aContext );
      myDrink = aDrink;
      setLayoutParams(new GridView.LayoutParams(70, 70));
      setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      setPadding(0, 0, 0, 0);
      setClickable( true );
      setOnClickListener( this );
      setImageResource( aDrink.getImageResource() );
      setContentDescription( Integer.toString(myDrinkList.getDrinkOrder( myDrink )) );
    }


    @Override
    public void onClick( View aView ) {
      if(myDrink.getSubSelections().size() > 0){
        myActivity.openContextMenu( this );
      } else {
        myDrinkList.addDrink( new DrinkOrder( myDrink ) );
      }
    }

    public Drink getDrink(){
      return myDrink;
    }

    @Override
    protected void onDraw( Canvas aCanvas ) {
      super.onDraw( aCanvas );
      Paint thePaint = new Paint();
      thePaint.setColor( Color.GRAY );
//      aCanvas.drawText( myDrink.getName(), 0, myDrink.getName().length(), 8f, getHeight() - 10, thePaint);
      
//      thePaint.setColor( Color.BLUE );
      if(myDrinkList.getDrinkOrder( myDrink ) > 0){
        String theNumberOfDrinks = Integer.toString(myDrinkList.getDrinkOrder( myDrink ));
        aCanvas.drawText( theNumberOfDrinks, 0, theNumberOfDrinks.length(), 10, 15, thePaint);
      }
    }

    @Override
    protected void onCreateContextMenu( ContextMenu aMenu ) {
      myActivity.setCurrentDrink( myDrink );
      aMenu.clear();
      for(String theSubSelection : myDrink.getSubSelections()){
        aMenu.add( theSubSelection );
      }
    }
  }
}

