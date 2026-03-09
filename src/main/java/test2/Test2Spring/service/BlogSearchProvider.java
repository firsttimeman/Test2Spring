package test2.Test2Spring.service;


import reactor.core.publisher.Mono;

public interface BlogSearchProvider {
    Mono<Object> search(String query, String sort, int page, int size);
}