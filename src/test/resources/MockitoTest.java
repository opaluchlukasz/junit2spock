package foo.bar;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyIterable;
import static org.mockito.Matchers.anyObject;

public class MockitoTest {

    @Mock
    public SomeClass mocked;
    @Mock
    public List<String> mockedList;

    @Before
    public void before() {
        when(mockedList.get(1)).thenReturn("second");
        when(mockedList.get(2)).thenReturn("second", "third", "four");
        when(mockedList.get(3)).thenThrow(new IndexOutOfBoundsException("out of bound"));
        given(mockedList.get(0)).willReturn("some");
    }

    @Test
    public void mockingTest() {
        LinkedList mockedList = mock(LinkedList.class);
        if (mockedList != null) {
            when(mockedList.get(0)).thenReturn("first");
        }

        org.mockito.Mockito.verify(mockedList).get(0);
        verify(mockedList).get(anyObject());
        verify(mocked).someMethod(anyBoolean(), anyByte(), anyChar(), anyInt(), anyLong(), anyFloat(), anyDouble());
        verify(mocked).someOtherMethod(anyShort(), anyString(), anyList(), anySet(), anyMap(), anyCollection(), anyIterable());
        verify(mocked).someOtherOtherMethod(any(), any(String.class), isA(String.class));
        verify(mockedList, never()).clear();
        verify(mockedList, times(cardinality())).size();
        verify(mockedList, atLeastOnce()).size();
        verify(mockedList, Mockito.atLeast(2)).size();
        verify(mockedList, org.mockito.Mockito.atMost(4)).size();
        verifyNoMoreInteractions(mockedList);
    }

    private Integer cardinality() {
        return 2;
    }
}
