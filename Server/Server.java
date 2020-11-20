package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer(777);
    }

    private void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new closingThread().start();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();
            }
        } catch (SocketException e) {
            System.out.println("Server closed.");
        }
    }

    private class closingThread extends Thread {
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        public void run() {
            while (true) {
                try {
                    String command = reader.readLine();
                    if (command.equals("exit")) {
                        for(Map.Entry<String, ClientHandler> entry: clientHandlers.entrySet()) {
                            entry.getValue().getOut().writeObject("Server closed. Please leave.");
                        }
                        serverSocket.close();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String username;

        ObjectOutputStream getOut() {
            return out;
        }

        public ClientHandler(Socket socket) {
            clientSocket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                do {
                    username = (String) in.readObject();
                    out.writeObject(isUsernameFree(username));
                }
                while (!isUsernameFree(username));
                notifyUsersAndAdd();

                String receivedMessage;
                while (!(receivedMessage = (String) in.readObject()).matches("exit")) {
                    for(Map.Entry<String, ClientHandler> entry: clientHandlers.entrySet()) {
                            entry.getValue().out.writeObject(receivedMessage);
                    }

                }
                notifyUsersAndRemove();
                closeClient();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void notifyUsersAndAdd() throws IOException {
            System.out.println(username + " has joined the chat.");
            for(Map.Entry<String, ClientHandler> entry: clientHandlers.entrySet()) {
                entry.getValue().getOut().writeObject(username + " has joined the chat.");
            }
            clientHandlers.put(username, this);
        }

        private void notifyUsersAndRemove() throws IOException {
            clientHandlers.remove(username);
            System.out.println(username + " has left the chat.");
            for(Map.Entry<String, ClientHandler> entry: clientHandlers.entrySet()) {
                entry.getValue().getOut().writeObject(username + " has left the chat.");
            }
        }

        private boolean isUsernameFree(String username) {
            for(Map.Entry<String, ClientHandler> entry: clientHandlers.entrySet()) {
                if (username.equals(entry.getKey()))
                    return false;
            }
            return true;
        }

        private void closeClient() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }
}
