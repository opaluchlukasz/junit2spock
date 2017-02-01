package foo.bar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyTest {

    @Mock
    public Object mocked;

    @Test
    public void multiplicationOfZeroIntegersShouldReturnZero() {
        MyClass tester = new MyClass();

        junit.framework.Assert.assertEquals("10 x 0 must be 0", 0, tester.multiply(10, 0));
        assertEquals("0 x 10 must be 0", 0, tester.multiply(0, 10));
        assertEquals("0 x 0 must be 0", 0, hashocde());
        assertEquals("some string", new String("some string"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void empty() {
        new ArrayList<Object>().get(0);
    }

    private String helper() {
        return "bar";
    }
}
