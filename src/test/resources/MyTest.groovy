package foo.bar
import spock.lang.Specification

class MyTest extends Specification {
	Object mocked=Mock(Object)

	def setup() {
		mockedList.get(1) >> 'second'
		mockedList.get(2) >> {
			throw new IndexOutOfBoundsException('out of bound')
		}
		mockedList.get(0) >> 'some'
	}

	def 'multiplication of zero integers should return zero'() {
		given:
		MyClass tester=new MyClass()

		expect:
		tester.multiply(10,0) == 0
		tester.multiply(0,10) == 0
		hashocde() == 0
		new String('some string') == 'some string'
		new int[1] == new int[1]
		!false
		null == null
	}

	def 'empty'() {
		when:
		new ArrayList<Object>().get(0)

		then:
		thrown(IndexOutOfBoundsException)
	}

	def 'mocking test'() {
		given:
		LinkedList mockedList=Mock(LinkedList)

		if (mockedList != null) {
			mockedList.get(0) >> 'first'
		}

		expect:
		mockedList.get(0) == 'first'
		_ * mockedList.get(0)
	}

	private String helper() {
		return 'bar'
	}

}
