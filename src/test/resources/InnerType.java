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

    public static interface InnerInterface extends SomeOtherInterface {
        void method(Integer argument);
    }
}
