package problem1;

import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;

public class CommunicationTest {
    @Test
    public void testCommunication() throws Exception {
        final String[] strings = {"Hello, World!", null, null};
        final int port = 9999;

        CyclicBarrier serverWaitingBarrier = new CyclicBarrier(2);
        CyclicBarrier globalBarrier = new CyclicBarrier(3);

        Runnable serverRunnable = () -> {
            try (ServerSocket server = new ServerSocket(port)) {
                serverWaitingBarrier.await();
                Socket socket = server.accept();
                Communication com = new Communication(socket);
                strings[1] = com.receive();
                com.send(strings[1]);
                globalBarrier.await();
                socket.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Runnable clientRunnable = () -> {
            try (Socket socket = new Socket("localhost", port)) {
                Communication com = new Communication(socket);
                com.send(strings[0]);
                strings[2] = com.receive();
                globalBarrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        new Thread(serverRunnable).start();
        serverWaitingBarrier.await();
        new Thread(clientRunnable).start();

        globalBarrier.await();

        System.out.println(strings[2]);
        assertEquals(strings[0], strings[1]);
        assertEquals(strings[0], strings[2]);
    }
}
