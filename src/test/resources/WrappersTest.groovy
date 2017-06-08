package foo.bar
import spock.lang.Specification

class WrappersTest extends Specification {
	private Object object

	def 'if wrapper test'() {
		given:
		LinkedList mockedList=Mock(LinkedList)

		if (mockedList != null) {
			if (mockedList != null) {
				mockedList.get(0) >> 'first'
			}
		} else 	if (mockedList == null) {
			mockedList.get(0) >> 'first'
			mockedList.get(1) >> 'second'
		}


		expect:
		mockedList.get(0) == 'first'
	}

	def 'try wrapper test'() {
		given:
		try {
			BufferedReader br=new BufferedReader(new FileReader(''))
			object.toString()
		} catch(NullPointerException|IllegalArgumentException exception) {
			throw exception
		} catch(Exception exception) {
			throw exception
		}

		expect:
		thrown(Exception)
	}

}
