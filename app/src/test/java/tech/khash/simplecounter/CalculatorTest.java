package tech.khash.simplecounter;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *Local unit test for bpm calculator class
 */


@RunWith(JUnit4.class)
@SmallTest
public class CalculatorTest {

    private Counter counter;

    /**
     * Set up the environment for testing
     */
    @Before
    public void setUp() {
        counter = new Counter();
    }

    /**
     * Test for converting elapsed long to 3 decimal String
     */
    @Test
    public void floatToStringConversionTest() {

        String resultConvert = Counter.getTestElapsedDecimalString(2946);
        assertThat(resultConvert, is(equalTo("2.946")));
    }//bpmCalculator

    @Test
    public void getBpmTest() {
        int bpm = getBpm(4, 4000);
        assertThat(bpm, is(equalTo(60)));
    }//getBpm

    private int getBpm (int count, long elapsed) {

        float bmpFloat = (count * 60000.0f) / elapsed;
        int bpm = (int) bmpFloat;
        return bpm;
    }


}//BpmCalculatorTest