package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SomeService {
    private static final Logger log = LoggerFactory.getLogger( SomeService.class );
    private AtomicBoolean finished = new AtomicBoolean( false );
    private AtomicBoolean working = new AtomicBoolean( false );


    public boolean getFinished() {
        return finished.get();
    }

    public boolean getWorking() {
        return working.get();
    }

    @Async
    public Future<Integer> asyncMethod() {
        log.info( "Working" );
        working.set( true );
        try {
            while (!Thread.currentThread().isInterrupted())
                Thread.sleep( 100 );
        } catch (InterruptedException e) {
            log.info( "Interrupted" );
        }
        finished.set( true );
        return new CompletableFuture<>();
    }
}
