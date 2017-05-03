package foo.bar

class InnerType {


	private static class SomeStaticClass extends SomeClass {

		SomeStaticClass(Optional<SomeClass> next) {
			super(next)
		}

		@Override String apply(String line) {
			return line.reverse()
		}

	}

	private class SomeOtherClass {

		protected String apply(String line) {
			return line.reverse()
		}

	}

	static interface InnerInterface {
		void method(Integer argument)
	}
}
