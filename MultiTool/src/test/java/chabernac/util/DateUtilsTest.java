package chabernac.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;


public class DateUtilsTest {
    
    @Test
    public void getWorkingDaysSameDate() {
        long now = new Date().getTime();
        Assert.assertEquals( 1, DateUtils.getWorkingDaysCovered( now, now ) );
    }
    
    @Test
    public void getWorkingDaysSameDate2() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
        Assert.assertEquals( 1, DateUtils.getWorkingDaysCovered( format.parse( "04/08/2017" ).getTime(), format.parse( "04/08/2017" ).getTime() ) );
    }
    
    @Test
    public void getWorkingDaysSlightDifference(){
        long now = new Date().getTime();
        Assert.assertEquals( 1, DateUtils.getWorkingDaysCovered( now, now + 1) );
    }
    
    @Test
    public void getWorkingDaysOneDayDiffernce(){
        long now = new Date().getTime();
        Assert.assertEquals( 2, DateUtils.getWorkingDaysCovered( now, now + 24 * 60 * 60 * 1000 + 1) );
    }
}
