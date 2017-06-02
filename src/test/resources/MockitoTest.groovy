package foo.bar
import spock.lang.Specification
import org.mockito.Mockito.ArgumentMatcher
import org.mockito.internal.matchers.LessOrEqual
import static org.mockito.Matchers.intThat

class MockitoTest extends Specification {
	SomeClass mocked=Mock(SomeClass)
	List<String> mockedList=Mock(List)
	PersonDao personDao=Mock(PersonDao)

	def setup() {
		mockedList.get(1) >> 'second'
		mockedList.get(2) >>> ['second', 'third', 'four']
		mockedList.get(3) >> {
			throw new IndexOutOfBoundsException('out of bound')
		}
		mockedList.get(4) >> {
			throw new IndexOutOfBoundsException()
		}
		mockedList.get(0) >> 'some'
		personDao.save({ Object argument ->
			return 'Spock'.equalsIgnoreCase(((Person)argument).getName())
		} as Person) >> true
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
		1 * mocked.someOtherOtherMethod(_,_ as String,_ as String,null,!null,!null,_ as List<Object>,_ as Set<Object>,_ as Collection<Object>,_ as Map<Long,List>)
		1 * mocked.someOtherOtherOtherMethod({
			it.startsWith('prefix')
		} as String)
		1 * mocked.method1({ Integer actual ->
			return actual < 4
		} as Integer)
		1 * mocked.method1(intThat(new LessOrEqual<>(4)))
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
