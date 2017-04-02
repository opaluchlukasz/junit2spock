package foo.bar;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class MockitoTest {

    @Mock
    public Object mocked;

    @Before
    public void before() {
        when(mockedList.get(1)).thenReturn("second");
        when(mockedList.get(2)).thenThrow(new IndexOutOfBoundsException("out of bound"));
        given(mockedList.get(0)).willReturn("some");
    }

    @Test
    public void mockingTest() {
        LinkedList mockedList = mock(LinkedList.class);
        if (mockedList != null) {
            when(mockedList.get(0)).thenReturn("first");
        }

        verify(mockedList).get(0);
        verify(mockedList, never()).clear();
        verify(mockedList, times(cardinality())).size();
        verify(mockedList, atLeastOnce()).size();
        verify(mockedList, atLeast(2)).size();
        verify(mockedList, atMost(4)).size();
    }

    private Integer cardinality() {
        return 2;
    }
}
