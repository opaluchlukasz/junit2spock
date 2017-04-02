package foo.bar
import spock.lang.Specification

class MockitoTest extends Specification {
	Object mocked=Mock(Object)

	def setup() {
		mockedList.get(1) >> 'second'
		mockedList.get(2) >> {
			throw new IndexOutOfBoundsException('out of bound')
		}
		mockedList.get(0) >> 'some'
	}

	def 'mocking test'() {
		given:
		LinkedList mockedList=Mock(LinkedList)

		if (mockedList != null) {
			mockedList.get(0) >> 'first'
		}

		expect:
		1 * mockedList.get(0)
		0 * mockedList.clear()
		cardinality() * mockedList.size()
		(1 .. _) * mockedList.size()
		(2 .. _) * mockedList.size()
		(_ .. 4) * mockedList.size()
	}

	private Integer cardinality() {
		return 2
	}

}
