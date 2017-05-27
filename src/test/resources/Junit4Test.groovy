package foo.bar
import spock.lang.Specification
import static java.lang.Integer.parseInt

class Junit4Test extends Specification {

	def 'multiplication of zero integers should return zero'() {
		given:
		MyClass tester=new MyClass()

		expect:
		tester.multiply(parseInt('10'),0) == 0
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

	private static String helper() {
		return 'bar'
	}

}
