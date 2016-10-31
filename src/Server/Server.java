package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.sql.*;



/**
 * Created by dyachkov1997 on 24.10.16.
 */
public class Server {

    private static ArrayList<PrintWriter> streams;
    private static Connection connection;
    private static java.sql.Statement statement;
   // private static ResultSet resultSet;



    public static void main(String[] args) {
        start();
    }
    public static void start() {

        streams = new ArrayList<>();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New User!");
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                streams.add(writer);

                Thread thread = new Thread(new Listener(socket));
                thread.start();


            }
        } catch (Exception e) {
            System.out.println("Error in server creating");
        }

    }

    private static class Listener implements Runnable {

        BufferedReader reader;

        public Listener(Socket socket) {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                System.out.println("Error in Listener in Server 144");
            }
        }
        @Override
        public void run() {
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                    tellEveryone(message);

                }
            } catch (Exception e) {
                System.out.println("Error in Listener in Server 256");
            }
        }
    }

    private static void tellEveryone(String message) {
        String login = message.substring(0, message.indexOf(":"));

        System.out.println("befo");
        save(login, message);
        System.out.println("after");

        for (PrintWriter currentWriter : streams) {
            currentWriter.println(message);
            currentWriter.flush();
        }
    }

    private static void save(String login, String message) {
        setDB();

        String sql = "INSERT INTO `chat` (`login`, `message`) VALUES ('"+login+"', '"+message+"');";

        try {
            statement.executeUpdate(sql);
            System.out.println(sql);
        } catch (SQLException e) {
            System.out.println("sql error");
        }


    }

    private static void setDB() {

        String url = "jdbc:mysql://localhost:3306/Messanger";

        String login = "root";
        String pass = "root";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("asdad");
        } catch (Exception e) {
            System.out.println("ERROR 1488");
        }

        try {
            System.out.println("EWEDWEDEW2121212");
            connection = DriverManager.getConnection(url,login,pass);
            System.out.println("EWEDWEDEW");
        } catch (Exception e) {
            System.out.println("ERROR 1489");
        }
        System.out.println("connection is seted");
        try {
            statement = connection.createStatement();
        } catch (Exception e) {
            System.out.println("ERROR 1490");
        }


    }


}
