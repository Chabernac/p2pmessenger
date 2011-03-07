/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DrinksAdapter extends BaseAdapter implements OnClickListener {
  private Context mContext;
  private List<Drink> myDrinks = new ArrayList<Drink>();
  private DrinkList myDrinkList;

  public DrinksAdapter(Context c, String aDrinks, DrinkList aDrinkList) {
      mContext = c;
      myDrinkList = aDrinkList;
      loadImages(aDrinks);
  }
  
  private void loadImages(String aDrinks){
    Field[] theFields = R.drawable.class.getFields();
    for(Field theField : theFields){
      if(theField.getName().startsWith(aDrinks)){
        try {
          myDrinks.add(new Drink( theField.getName().substring( aDrinks.length() + 1 ), (Integer)theField.get(R.drawable.class)));
        } catch (Exception e) {
        }
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
    
    return new DrinkView(mContext, myDrinks.get(aPosition));
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
      myDrinkList.addDrink( myDrink );
    }
    
    public Drink getDrink(){
      return myDrink;
    }
  }
}

