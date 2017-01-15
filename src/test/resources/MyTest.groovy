package foo.bar;
import spock.lang.Specification;

class MyTests extends Specification {
	def 'multiplication of zero integers should return zero'() {
		given:
		MyClass tester=new MyClass();

		expect:
		multiply(10,0) == 0
		multiply(0,10) == 0
		multiply(0,0) == 0	}

	private String helper() {
		return "bar";
	}

}
