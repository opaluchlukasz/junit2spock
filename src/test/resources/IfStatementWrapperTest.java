package foo.bar;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.when;

public class IfStatementWrapperTest {

    @Test
    public void mockingTest() {
        LinkedList mockedList = mock(LinkedList.class);
        if (mockedList != null) {
            if (mockedList != null) {
                when(mockedList.get(0)).thenReturn("first");
            }
        } else if (mockedList == null) {
            when(mockedList.get(0)).thenReturn("first");
            when(mockedList.get(1)).thenReturn("second");
        }

        assertTrue(mockedList.get(0) == "first");
    }
}
