import org.junit.Test;

public class CommentsAsMarkerForBlocks {

    @Test
    public void shouldTreatCommentsAsMarkerForBlocks() {
        //given
        MyClass testee = new MyClass();

        //when
        testee.someMethod();

        //then
        testee.someOtherMethod();
    }

    @Test
    public void shouldHandleConsecutiveWhenThenBlocks() {
        //given
        MyClass testee = new MyClass();

        //when
        testee.someMethod();

        //then
        testee.someOtherMethod();

        //when
        testee.someMethod();

        //then
        testee.someOtherMethod();
    }

    @Test
    public void shouldIgnoreOtherComments() {
        //given that
        MyClass testee = new MyClass();

        //when it happens
        MyClass testee1 = new MyClass();

        //then this will happen
        MyClass testee2 = new MyClass();
    }
}
