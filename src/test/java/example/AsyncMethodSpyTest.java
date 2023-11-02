package example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.internal.util.MockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

public class AsyncMethodSpyTest {

	@Test
	@Timeout(10)
	public void test() throws InterruptedException {
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class)) {
			SomeService service = context.getBean(SomeService.class);
			SomeService spiedService = spy(service);
			assertThat(MockUtil.isSpy(spiedService)).isTrue();
			Future<Integer> future = spiedService.asyncMethod();
			System.out.println(future);
			while (!spiedService.getWorking()) {
				Thread.sleep(100);
			}
			future.cancel(true);
			while (!spiedService.getFinished()) {
				Thread.sleep( 100 );
			}
		}
	}

	@Configuration
	@EnableAsync(mode = AdviceMode.ASPECTJ)
	static class TestConfiguration {

		@Bean
		public SomeService someService() {
			return new SomeService();
		}

		@Bean
		public SimpleAsyncTaskExecutor taskExecutor() {
			return new SimpleAsyncTaskExecutor("Test--");
		}

	}

	static class SomeService {

		private static final Logger log = LoggerFactory.getLogger(SomeService.class);

		private AtomicBoolean finished = new AtomicBoolean(false);

		private AtomicBoolean working = new AtomicBoolean(false);


		public boolean getFinished() {
			return finished.get();
		}

		public boolean getWorking() {
			return working.get();
		}

		@Async
		public Future<Integer> asyncMethod() {
			log.info("Working");
			working.set(true);
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				log.info("Interrupted");
			}
			finished.set(true);
			CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
			System.out.println("Returning " + completableFuture);
			return completableFuture;
		}

	}

}