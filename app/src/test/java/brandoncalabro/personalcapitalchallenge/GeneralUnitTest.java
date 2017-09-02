package brandoncalabro.personalcapitalchallenge;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneralUnitTest {
    @Test
    public void dateFormat_isCorrect() throws Exception {
        assertEquals("September 01, 2017", CustomViewHelper.formatDateTime("Fri, 01 Sep 2017 22:00:30 +0000"));
    }
}