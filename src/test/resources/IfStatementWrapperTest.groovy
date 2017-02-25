package foo.bar
import spock.lang.Specification

class IfStatementWrapperTest extends Specification {

	def 'mocking test'() {
		given:
		LinkedList mockedList=Mock(LinkedList)

		if (mockedList != null) {
			if (mockedList != null) {
				when(mockedList.get(0)).thenReturn('first');
			}
		} else 		if (mockedList == null) {
			when(mockedList.get(0)).thenReturn('first');
			when(mockedList.get(1)).thenReturn('second');
		}


		expect:
		mockedList.get(0) == 'first'
	}

}
