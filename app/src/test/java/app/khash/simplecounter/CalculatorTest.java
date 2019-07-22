package app.khash.simplecounter;

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
    public void floatToStringConversion() {

        String resultConvert = Counter.getElapsedDecimalString(2946);
        assertThat(resultConvert, is(equalTo("2.946")));
    }//bpmCalculator


}//BpmCalculatorTest