package chabernac.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static int getWorkingDaysCovered( long startDate, long endDate ) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime( getDateWithoutTime( startDate ) );

        Calendar endCal = Calendar.getInstance();
        endCal.setTime( getDateWithoutTime( endDate ) );

        int workDays = 0;

        while ( !startCal.after( endCal ) ) {
            if ( isWeekDay( startCal ) ) {
                workDays++;
            }
            startCal.add( Calendar.DAY_OF_MONTH, 1 );

        }
        return workDays;
    }

    private static boolean isWeekDay( Calendar aCalendar ) {
        int dayOfWeek = aCalendar.get( Calendar.DAY_OF_WEEK );
        if ( dayOfWeek == Calendar.SATURDAY ) return false;
        if ( dayOfWeek == Calendar.SUNDAY ) return false;
        return true;
    }

    public static Date getDateWithoutTime( long aTime ) {
        try {
            Date theDate = new Date();
            theDate.setTime( aTime );
            SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
            return format.parse( format.format( theDate ) );
        } catch ( ParseException e ) {
            throw new IllegalArgumentException( "Could not parse given time '" + aTime + "' to a date" );
        }
    }

}
