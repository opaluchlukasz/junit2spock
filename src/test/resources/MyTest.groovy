package foo.bar;
import spock.lang.Specification;

class MyTest extends Specification {
	def 'multiplication of zero integers should return zero'() {
		given:
		MyClass tester=new MyClass()

		expect:
		multiply(10,0) == 0
		multiply(0,10) == 0
		multiply(0,0) == 0	}

	def 'empty'() {
		when:
		new ArrayList<Object>().get(0)

		then:
		thrown(IndexOutOfBoundsException)	}

	private String helper() {
		return "bar"
	}

}
