# Theoretical Question

- The output will be the same for the *Atomic Counter* each time at 2000000 but Normal Counter will vary between one and two million. This is because atomicCounter is updated with a thread-safe operation but normalCounter is using `normalCounter++` which is non-thread-safe and causes **race condition** .
- The purpose of AtomicInteger is that it is safely updated the shared data in lock free condition without casing race condition .
- The atomicity of counter makes the read & write operations occur at one single step , this system works in a lock free place because it uses low level hardware instructions .
- Atomic variables are better for simple operations on single variables *But* locks would be better for coordinating complex logics .
- AtomicBoolean , AtomicInteger , AtomicLong , AtomicIntegerArray and some other simple data types are available . there are some reference types too like : AtomicReference\<V\> , AtomicReferenceArray\<E\> and so on .