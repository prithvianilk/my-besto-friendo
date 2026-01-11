# Wide Event Logging

- This doc will mention the wide event logging feature and its implementation.
- We will implement a wide event class, that will hold context in a ThreadLocal variable.
- This thread local must be encapsulated from the outside world, and there should be only methods to read / update it.
- The wide event context can be updated using a method, enrich(String key, Mergeable o);
  - A Mergable is an interface that supports the Mergable merge(Mergable other) method.
  - Internally, this will store it in a map<string, object> threadlocal, so and the key of the enrich method will be used to add it to the map.
  - I also want to support a merge operation, where if the key already exists, we will merge the values by first reading from the map, then merging, then updating the map.
- We want to log the wide event context at the end of a method, hence we will create an annotation that we can mark on methods, to log the wide event context at the end of the method. Call this, WithWideEventLogging.
- We will add this on the WhatsappMessageListener class, to log the wide event context at the end of the method.
- We can use 

```bash
cat /tmp/context-service.logs | grep Wide | sed 's/^[^{]*//' | jq | less
```

to parse the wide event logs.