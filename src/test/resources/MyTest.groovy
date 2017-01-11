package foo.bar;
import org.junit.Assert.assertEquals;
import spock.lang.Specification;

class MyTests extends Specification {
	def 'multiplication of zero integers should return zero'() {
		given:
		MyClass tester=new MyClass();

		expect:
		junit.framework.Assert.assertEquals("10 x 0 must be 0",0,tester.multiply(10,0));

		assertEquals("0 x 10 must be 0",0,tester.multiply(0,10));

		assertEquals("0 x 0 must be 0",0,tester.multiply(0,0));
	}

	private String helper() {
		return "bar";
	}

}
