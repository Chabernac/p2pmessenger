/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import chabernac.android.tools.Tools;

public class OrderedDrinksAdapter extends BaseAdapter{
  private final DrinkList myDrinkList;
  private final Activity myContext;

  public OrderedDrinksAdapter( Activity aContext, DrinkList aDrinkList ) {
    myContext = aContext;
    myDrinkList = aDrinkList;
    myDrinkList.registerObserver( new DrinkListObserver() );
  }

  @Override
  public int getCount() {
    return myDrinkList.getList().size();
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    LinearLayout theLinerLayout = new LinearLayout( myContext );
    theLinerLayout.setOrientation( LinearLayout.HORIZONTAL );
    AbsListView.LayoutParams theParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    theLinerLayout.setLayoutParams( theParams );
    theLinerLayout.setBackgroundColor( Color.YELLOW );
    
    DrinkOrder theDrink = myDrinkList.getDrinkAt( position );
    
    TextView theTextView = new TextView(myContext);
    theTextView.setTextColor(Color.BLACK);
    theTextView.setText( Integer.toString( theDrink.getNumberOfDrinks() ));
    theLinerLayout.addView( theTextView );
    
    
    theTextView = new TextView(myContext);
    theTextView.setTextColor(Color.BLACK);
    theTextView.setText( Tools.translate( myContext, theDrink.getName() ));
    theLinerLayout.addView( theTextView );
    
    Button theButton = new Button( myContext );
    theButton.setBackgroundDrawable( myContext.getResources().getDrawable( R.drawable.plus ) );
    theButton.setOnClickListener( new RemoveDrinkListener( theDrink ));
    theLinerLayout.addView( theButton);
    
    theButton = new Button( myContext );
    theButton.setBackgroundDrawable( myContext.getResources().getDrawable( R.drawable.plus ) );
    theButton.setOnClickListener( new RemoveDrinkListener( theDrink ));
    theLinerLayout.addView( theButton);
    
    return theLinerLayout;
  }

  private class RemoveDrinkListener implements OnClickListener{
    private final DrinkOrder myDrink;

    public RemoveDrinkListener(DrinkOrder aDrink){
      myDrink = aDrink;
    }

    @Override
    public void onClick( View aView ) {
      myDrinkList.removeDrink( myDrink );
    }
  }

  private class AddDrinkListener implements OnClickListener{
    private final DrinkOrder myDrink;

    public AddDrinkListener(DrinkOrder aDrink){
      myDrink = aDrink;
    }

    @Override
    public void onClick( View aView ) {
      myDrinkList.addDrink( myDrink );
    }
  }
  
  public class DrinkListObserver extends DataSetObserver {
    public void onChanged(){
     notifyDataSetChanged();
    }

  }



}
