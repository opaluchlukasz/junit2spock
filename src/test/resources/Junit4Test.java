package foo.bar;

import org.junit.Before;
import org.junit.Test;

import static java.lang.Integer.parseInt;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Junit4Test {

    @Test
    public void multiplicationOfZeroIntegersShouldReturnZero() {
        MyClass tester = new MyClass();

        junit.framework.Assert.assertEquals("10 x 0 must be 0", 0, tester.multiply(parseInt("10"), 0));
        assertEquals("0 x 10 must be 0", 0, tester.multiply(0, 10));
        assertEquals("0 x 0 must be 0", 0, hashocde());
        assertEquals("some string", new String("some string"));
        assertArrayEquals(new int[1], new int[1]);
        assertFalse(false);
        assertNull(null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void empty() {
        new ArrayList<Object>().get(0);
    }

    private static String helper() {
        return "bar";
    }
}
