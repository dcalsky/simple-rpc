Simple-RPC
====

## Get Started
Create service interface and its implement class:
```java
public interface GreetService {
     String greet(String greetWords);
}

@RpcService(GreetService.class)
public class GreetSerivceImpl implements GreetService {
    @Override
    public String greet(String greetWords) {
        return "Hello, " + greetWords;
    }
}
``` 


Launch RPC Server:
```java
var server = new RpcServer(7777);
server.addService(GreetSerivceImpl.class);

try {
    server.run();
} catch (Exception e) {
    System.out.println("Server error");
}
``` 

Create a RPC client:
```java
var client = new RpcClient(7777);
GreetService greetService = client.create(GreetService.class);
var result = greetService.greet("yihengz");
System.out.println(result);
// => Hello, yihengz.
```



