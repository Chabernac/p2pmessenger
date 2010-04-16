package chabernac.utils.sort;

/*
 * @(#)QSortAlgorithm.java      1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted.
 * Please refer to the file http://www.javasoft.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://www.javasoft.com/licensing.html for further important
 * licensing information for the Java (tm) Technology.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version     @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
 * extended with TriMedian and InsertionSort by Denis Ahrens
 * with all the tips from Robert Sedgewick (Algorithms in C++).
 * It uses TriMedian and InsertionSort for lists shorts than 4.
 * <fuhrmann@cs.tu-berlin.de>
 */

import java.util.Vector;

public class FastQSortAlgorithm
{
        /** This is a generic version of C.A.R Hoare's Quick Sort
        * algorithm.  This will handle arrays that are already
        * sorted, and arrays with duplicate keys.<BR>
        *
        * If you think of a one dimensional array as going from
        * the lowest index on the left to the highest index on the right
        * then the parameters to this function are lowest index or
        * left and highest index or right.  The first time you call
        * this function it will be with the parameters 0, a.length - 1.
        *
        * @param a         an integer array
        * @param lo0     left boundary of array partition
        * @param hi0     right boundary of array partition
        */
        private void QuickSort(Vector a, int l, int r) throws Exception
   {
        int M = 4;
        int i;
        int j;
        Object v;

        if ((r-l)>M)
        {
                i = (r+l)/2;
                if (((Comparable)a.elementAt(l)).compareTo((Comparable)a.elementAt(i)) > 0) swap(a,l,i);     // Tri-Median Methode!
                if (((Comparable)a.elementAt(l)).compareTo((Comparable)a.elementAt(r)) > 0) swap(a,l,r);
                if (((Comparable)a.elementAt(i)).compareTo((Comparable)a.elementAt(r)) > 0) swap(a,i,r);
                //Debug.log(this,"1");

                j = r-1;
                swap(a,i,j);
                i = l;
                v = a.elementAt(j);
                for(;;)
                {
					//Debug.log(this,"2");
                        while(((Comparable)a.elementAt(++i)).compareTo((Comparable)v) < 0);
                     //Debug.log(this,"6");
                        while(((Comparable)a.elementAt(--j)).compareTo((Comparable)v) > 0);
                      //Debug.log(this,"7");
                        if (j<i) break;
                         //Debug.log(this,"12");
                        swap (a,i,j);
                         //Debug.log(this,"13");
                        //pause(i,j);
                        //if (stopRequested) {
                        //    return;
                        //}
                }
                 //Debug.log(this,"8");
                swap(a,i,r-1);
                 //Debug.log(this,"9");
                //pause(i);
                QuickSort(a,l,j);
                 //Debug.log(this,"10");
                QuickSort(a,i+1,r);
                 //Debug.log(this,"11");
        }
}

        private void swap(Vector a, int i, int j)
        {
			//Debug.log(this,"4");
                Object T;
                T = (Object)a.elementAt(i);
				a.setElementAt(a.elementAt(j),i);
				a.setElementAt(T,j);
			//Debug.log(this,"5");

                //(Object)a.elementAt(i) = (Object)a.elementAt(j);
                //(Object)a.elementAt(j) = T;
        }

        private void InsertionSort(Vector a, int lo0, int hi0) throws Exception
        {
                int i;
                int j;
                Object v;

                for (i=lo0+1;i<hi0+1;i++)
                {
					//Debug.log(this,"3");
                        v = a.elementAt(i);
                         //Debug.log(this,"14");
                        j=i;
                        while ((j>lo0) && (((Comparable)a.elementAt(j-1)).compareTo((Comparable)v)>0))
                        {
							 //Debug.log(this,"15");
                                //a.elementAt(j) = a.elementAt(j-1);
                                a.setElementAt(a.elementAt(j-1),j);
                                 //Debug.log(this,"16");
                                //pause(i,j);
                                j--;
                        }
                        //a.elementAt(j) = v;
                         //Debug.log(this,"17 " + j + " " + a.size());
                        a.setElementAt(v,j);
                        //Debug.log(this,"18 " +  i + "<=" + hi0);
                }
                //Debug.log(this,"19");
        }

        public void  sort(Vector a) throws Exception
        {
			//Debug.log(this,"20");

                QuickSort(a, 0, a.size() - 1);
                //Debug.log(this,"21");
                InsertionSort(a,0,a.size() - 1);
                //Debug.log(this,"22");

              printVector(a);

                //pause(-1,-1);

        }


		private  void printVector(Vector vector)
		{
			//Debug.log(this,"\nGESORTEERDE LIST:");
			for(int i=0;i<vector.size();i++)
			{
				//Debug.log(this,(String)vector.elementAt(i));
			}
		}
}

