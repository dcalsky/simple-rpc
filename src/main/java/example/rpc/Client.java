package example.rpc;

import rpc.client.RpcClient;

public class Client {
    public static void main(String[] args) throws Exception {
        var client = new RpcClient(7777);
        GreetService greetService = client.create(GreetService.class);
        var result = greetService.greet("yihengz");
        System.out.println(result);
    }
}
