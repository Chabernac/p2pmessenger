/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.testapp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class DrinksAdapter extends BaseAdapter {
  private Context mContext;
  private List<Integer> myImages = new ArrayList<Integer>();

  public DrinksAdapter(Context c, String aDrinks) {
      mContext = c;
      loadImages(aDrinks);
  }
  
  private void loadImages(String aDrinks){
    Field[] theFields = R.drawable.class.getFields();
    for(Field theField : theFields){
      if(theField.getName().startsWith(aDrinks)){
        try {
          myImages.add((Integer)theField.get(R.drawable.class));
        } catch (Exception e) {
        }
      }
    }
  }

  public int getCount() {
      return myImages.size();
  }

  public Object getItem(int position) {
      return null;
  }

  public long getItemId(int position) {
      return 0;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
      ImageView imageView;
      if (convertView == null) {  // if it's not recycled, initialize some attributes
          imageView = new ImageView(mContext);
          imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
          imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
          imageView.setPadding(2, 2, 2, 2);
      } else {
          imageView = (ImageView) convertView;
      }

      imageView.setImageResource(myImages.get(position));
      return imageView;
  }

}

