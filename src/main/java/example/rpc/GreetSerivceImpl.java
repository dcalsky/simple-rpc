package example.rpc;

import rpc.RpcService;

@RpcService(GreetService.class)
public class GreetSerivceImpl implements GreetService {
    @Override
    public String greet(String greetWords) {
        return "Hello, " + greetWords;
    }
}
