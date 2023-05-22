## Sizer.sizeof

### What is this?

A tool to measure memory usage of Java objects at runtime by traversing the object graph and adding up the bytes for fields and objects.

### Why would anyone want to use this?

- Investigate heap usage problems. Profilers are good for looking at heap-wide histograms and finding allocation hotspots, but can't easily drill down to specific objects. Also, taking a heap dump usually requires halting execution.
- Planning.
  - e.g. What's the memory overhead of caching X?
- Curiosity?

### Why another Java object graph sizer?

I wanted a tool that met the following criteria:

- Accuracy
- Performance
- Clear licensing (Apache License, 2.0)
- Stability
- Hosted in a public repo
- No dependencies

### How to add to your project

You can either build from scratch using `mvn`:

1. Clone this repo
2. `mvn package`
3. Add `target/javasizer-VERSION.jar` to your classpath

### How to use

Add this to your JVM arguments:
```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```


`org.jh.Sizer.sizeof(root)` will return the bytes used by the object graph.
