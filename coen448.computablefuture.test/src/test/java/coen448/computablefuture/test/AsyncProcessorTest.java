package coen448.computablefuture.test;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.concurrent.*;

public class AsyncProcessorTest {
    @Test
    public void testProcessAsyncSuccess() throws ExecutionException, InterruptedException {
        Microservice mockService1 = mock(Microservice.class); //
        Microservice mockService2 = mock(Microservice.class);

        // any is from Mockito library, any input generated, is retaining its value
        // even if microservice isnt ready, this is how we override it to test it? 
        when(mockService1.retrieveAsync(any())).thenReturn(CompletableFuture.completedFuture("Hello"));// input and output "hello"
        when(mockService2.retrieveAsync(any())).thenReturn(CompletableFuture.completedFuture("World"));

        AsyncProcessor processor = new AsyncProcessor();

        // we cannot guarantee the order of completion , however, when we compose all the results, we want to follow this order
        // No matter who finishes first
        CompletableFuture<String> resultFuture = processor.processAsync(List.of(mockService1, mockService2)); 
        
        // in List.of it makes sure to follow a certain order of results despite the uncertainty related to which mockservice finishes first 
        String result = resultFuture.get();
        assertEquals("Hello World", result);
    }
}