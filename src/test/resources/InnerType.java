package foo.bar;

import java.util.Optional;

public class InnerType {

    private static class SomeStaticClass extends SomeClass {
        SomeStaticClass(Optional<SomeClass> next) {
            super(next);
        }

        @Override
        public String apply(String line) {
            return line.reverse();
        }
    }

    private class SomeOtherClass {
        protected String apply(String line) {
            return line.reverse();
        }
    }
}
