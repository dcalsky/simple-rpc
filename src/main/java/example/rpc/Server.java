package example.rpc;

import rpc.server.RpcServer;

public class Server {
    public static void main(String[] args) {
        var server = new RpcServer(7777);
        server.addService(GreetSerivceImpl.class);

        try {
            server.run();
        } catch (Exception e) {
            System.out.println("Server error");
        }
    }
}
