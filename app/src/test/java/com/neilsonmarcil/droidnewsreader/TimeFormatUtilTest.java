package com.neilsonmarcil.droidnewsreader;

import com.neilsonmarcil.droidnewsreader.util.TimeFormatUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the TimeFormatUtil static classes with different time.
 */
@RunWith(JUnit4.class)
public class TimeFormatUtilTest {

    @Test
    public void firstUnitSmall() {
        long time = 3000; // 3 seconds
        String res = TimeFormatUtil.formatTimespanAgoFirstUnit(time);
        assertThat(res, is("3 seconds ago"));

        time = 1000 * 4 * 60; // 4 minutes
        res = TimeFormatUtil.formatTimespanAgoFirstUnit(time);
        assertThat(res, is("4 minutes ago"));

        time = time * 60 + (1000); // 4 hours 1 sec
        res = TimeFormatUtil.formatTimespanAgoFirstUnit(time);
        assertThat(res, is("4 hours ago"));
    }

    @Test
    public void firstUnitMedium() {
        long time = 3000 + 300; // 3 seconds 300 ms
        String res = TimeFormatUtil.formatTimespanAgo(time);
        assertThat(res, is("3 seconds 300 milliseconds ago"));

        time = 120000 + 2000; // 2 minute 2 second
        res = TimeFormatUtil.formatTimespanAgo(time);
        assertThat(res, is("2 minutes 2 seconds ago"));

        time = (60000 * 60 * 2) + (60000 * 45) + (1000 * 22) + (554); // 2h 45m 22s 554ms
        res = TimeFormatUtil.formatTimespanAgo(time);
        assertThat(res, is("2 hours 45 minutes 22 seconds 554 milliseconds ago"));
    }

    @Test
    public void singleTimeUnit() {
        long time = 1000; // 1 second
        String res = TimeFormatUtil.formatTimespanAgo(time);
        assertThat(res, is("1 second ago"));

        time = 1000 * 60; // 1 minute
        res = TimeFormatUtil.formatTimespanAgo(time);
        assertThat(res, is("1 minute ago"));

        time = (1000 * 60 * 60) + (60000); // 1h 1min;
        res = TimeFormatUtil.formatTimespanAgo(time);
        assertThat(res, is("1 hour 1 minute ago"));
    }
}
