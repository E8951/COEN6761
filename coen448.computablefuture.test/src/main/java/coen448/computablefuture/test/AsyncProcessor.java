package coen448.computablefuture.test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncProcessor {
    public CompletableFuture<String> processAsync(List<Microservice> microservices) {
       
       // list of programing , bunch of processes
       // this is a pipeline based programing: u dont need to go with indexing
       // But you go through iteration, when you're done you go to the next one
       //pipline allows to go throw each elements one by one
       // when each object finished its function, they all need to synchronise through the collector
       
        List<CompletableFuture<String>> futures = microservices.stream() 
        // stream creats 3 microservices, it seperates them into three objects

        // example in range based for loop
        // for (auto item : array){
        // item.getName()
        //}
            .map(client -> client.retrieveAsync("hello")) // go through each object
            .collect(Collectors.toList()); // the synchronization part of all the objects

    
            // one the result is one by one finished, we need to put them in future
            // function ask me do we need all of them to finish? 
            // what if one of them fails
            // the allof insist that all of them pass, and if one of them fail, it will run forever
            // if u want it to ignore one if it fails, allOf needs to be changed. 
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))


        // the below is a pipline that collects the results and make it into an array and seperating values by a space
            .thenApply(v -> futures.stream() // this wait for all the hellos of all the objects
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" ")));
    }
}