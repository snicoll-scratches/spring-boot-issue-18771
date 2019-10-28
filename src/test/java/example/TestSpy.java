package example;

import org.mockito.internal.util.MockUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.concurrent.Future;

import static org.testng.Assert.*;

@SpringBootTest
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class TestSpy extends AbstractTestNGSpringContextTests {
    @SpyBean SomeService service;

    @Test(timeOut = 10000)
    public void test() throws InterruptedException {
        assertTrue( MockUtil.isSpy( service ) );
        Future<Integer> future = service.asyncMethod();
        while (!service.getWorking())
            Thread.sleep( 100 );
        future.cancel( true );
        while (!service.getFinished())
            Thread.sleep( 100 );
    }
}
