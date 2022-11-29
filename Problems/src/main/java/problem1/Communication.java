package problem1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Communication {
    private final Socket socket;

    public Communication(Socket socket) {
        this.socket = socket;
    }

    public <T> void send(T data) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(data);
    }

    public <T> T receive() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        return (T) in.readObject();
    }
}
