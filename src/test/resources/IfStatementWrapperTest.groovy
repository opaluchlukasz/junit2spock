package foo.bar
import spock.lang.Specification

class IfStatementWrapperTest extends Specification {

	def 'mocking test'() {
		given:
		LinkedList mockedList=Mock(LinkedList)

		if (mockedList != null) {
			if (mockedList != null) {
				mockedList.get(0) >> 'first'
			}
		} else 		if (mockedList == null) {
			mockedList.get(0) >> 'first'
			mockedList.get(1) >> 'second'
		}


		expect:
		mockedList.get(0) == 'first'
	}

}
