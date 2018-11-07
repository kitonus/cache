# com.github.kitonus.cache:cache-distributed
Distributed Spring cache based on Hazelcast and Caffeine

*Compatibility*
- Spring Boot 2.1.x. Current source code is compile-time-compatible with spring boot 2.0.x but it is not runtime-tested

*Capabilities*
- Cache entries are distributed (put once read everywhere)
- Minimize stale entries by using cache dependencies. An eviction in a cache will evict all caches that depend on it.
- Cache entries can be almost any type of object (no need to be java.io.Serializable objects - as long as JSON serializable and deserializable)
