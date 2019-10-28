package example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

@SpringBootTest
public class TestNormal extends AbstractTestNGSpringContextTests {
    @Autowired SomeService service;

    @Test(timeOut = 10000)
    public void test() throws InterruptedException {
        Future<Integer> future = service.asyncMethod();
        while (!service.getWorking())
            Thread.sleep( 100 );
        future.cancel( true );
        while (!service.getFinished())
            Thread.sleep( 100 );
    }
}
