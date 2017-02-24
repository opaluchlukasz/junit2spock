package foo.bar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

public class MyTest {

    @Mock
    public Object mocked;

    @Before
    public void before() {
        when(mockedList.get(1)).thenReturn("second");
        when(mockedList.get(2)).thenThrow(new IndexOutOfBoundsException("out of bound"));
        given(mockedList.get(0)).willReturn("some");
    }

    @Test
    public void multiplicationOfZeroIntegersShouldReturnZero() {
        MyClass tester = new MyClass();

        junit.framework.Assert.assertEquals("10 x 0 must be 0", 0, tester.multiply(10, 0));
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

    @Test
    public void mockingTest() {
        LinkedList mockedList = mock(LinkedList.class);
        if (mockedList != null) {
            when(mockedList.get(0)).thenReturn("first");
        }

        assertTrue(mockedList.get(0) == "first");
    }

    private String helper() {
        return "bar";
    }
}
