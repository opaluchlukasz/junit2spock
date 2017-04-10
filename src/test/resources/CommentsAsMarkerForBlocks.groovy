import spock.lang.Specification

class CommentsAsMarkerForBlocks extends Specification {

	def 'should treat comments as marker for blocks'() {
		given:
		MyClass testee=new MyClass()

		when:
		testee.someMethod()

		then:
		testee.someOtherMethod()

	}

	def 'should handle consecutive when then blocks'() {
		given:
		MyClass testee=new MyClass()

		when:
		testee.someMethod()

		then:
		testee.someOtherMethod()

		when:
		testee.someMethod()

		then:
		testee.someOtherMethod()

	}

	def 'should ignore other comments'() {
		expect:
		MyClass testee=new MyClass()

		MyClass testee1=new MyClass()

		MyClass testee2=new MyClass()

	}

}
