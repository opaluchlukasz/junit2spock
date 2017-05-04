package foo.bar
import spock.lang.Specification

class MockitoTest extends Specification {
	SomeClass mocked=Mock(SomeClass)
	List<String> mockedList=Mock(List)

	def setup() {
		mockedList.get(1) >> 'second'
		mockedList.get(2) >>> ['second', 'third', 'four']
		mockedList.get(3) >> {
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
		1 * mockedList.get(_)
		1 * mocked.someMethod(_ as Boolean,_ as Byte,_ as Character,_ as Integer,_ as Long,_ as Float,_ as Double)
		1 * mocked.someOtherMethod(_ as Short,_ as String,_ as List,_ as Set,_ as Map,_ as Collection,_ as Iterable)
		1 * mocked.someOtherOtherMethod(_,_ as String)
		0 * mockedList.clear()
		cardinality() * mockedList.size()
		(1 .. _) * mockedList.size()
		(2 .. _) * mockedList.size()
		(_ .. 4) * mockedList.size()
		0 * mockedList._
	}

	private Integer cardinality() {
		return 2
	}

}
