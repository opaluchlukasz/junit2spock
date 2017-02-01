package foo.bar;
import spock.lang.Specification;

class MyTest extends Specification {
	public Object mocked=Mock(Object.class);

	def 'multiplication of zero integers should return zero'() {
		given:
		MyClass tester=new MyClass()

		expect:
		tester.multiply(10,0) == 0
		tester.multiply(0,10) == 0
		hashocde() == 0
		new String("some string") == "some string"	}

	def 'empty'() {
		when:
		new ArrayList<Object>().get(0)

		then:
		thrown(IndexOutOfBoundsException)	}

	private String helper() {
		return "bar"
	}

}
