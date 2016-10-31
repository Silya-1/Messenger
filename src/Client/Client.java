package Client;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by dyachkov1997 on 22.10.16.
 */
public class Client {
    private static JTextArea textArea;
    private static JTextField textField;

    private static BufferedReader reader;      //отправляет сообщения на сервер
    private static PrintWriter writer;         //получает сообщения с сервера

    private static String nickname;

    public static void main(String[] args) {

        start();
    }

    public static void start() {
        /*
        ImageIcon imageIcon = new ImageIcon("src/logo.png");
        JOptionPane.showMessageDialog(null, imageIcon, "Messanger", -1);
        */
        nickname = JOptionPane.showInputDialog("Введите свой никнейм");

        JFrame frame = new JFrame("Messenger 1.0");
        frame.setResizable(false);                   //нельзя изменять размеры окна
        frame.setLocationRelativeTo(null);           //расположение по центру экрана

        JPanel panel = new JPanel();

        textArea = new JTextArea(15, 30);
        textArea.setLineWrap(true);                  //перенос строки при ситуации когда текст не влезает в область
        textArea.setEditable(false);                 //невозможность редактирования, только отображение текста
        textArea.setWrapStyleWord(true);             //перенос по словам

        //*****************************************************************************
        //        <- делаем область для отображения сообщений скролящейся ->
        JScrollPane scrollPane = new JScrollPane(textArea);

        //<- показывает вертикальную полосу прокрутки только если это необходимо ->
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //<- не отображаем горизонтальную полосу прокрутки ->
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //*****************************************************************************

        textField = new JTextField(20);

        //*****************************************************************************
        //                      <- <- Кнопки -> ->
        JButton sendButton = new JButton("Отправить");
        JButton reButton = new JButton("Обновить");

        reButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writer.print("");
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = nickname + ": " + textField.getText();
                writer.println(message);
                writer.flush();

                textField.setText("");      //очищаем поле после отправки
                textField.requestFocus();   //делаем активным для ввода сообщения
            }
        });
        //*****************************************************************************

        //*****************************************************************************
        panel.add(scrollPane);          //Прикрепляем скролящееся окно с сообщениями.
        panel.add(textField);           //А также панельку для ввода сообщения...
        panel.add(sendButton);          //... и кнопку отправить
        //*****************************************************************************

        setNet();

        Thread thread = new Thread(new Listener());
        thread.start();

        frame.getContentPane().add(BorderLayout.CENTER, panel);         // Прикрепляем панельку по центру фрэйма,
        frame.getContentPane().add(BorderLayout.NORTH, reButton);       // а кнопочку обновить сверху

        frame.setSize(400, 340);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void setNet() {
        try {

            Socket sock = new Socket("127.0.0.1", 5000);

            InputStreamReader sockReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(sockReader);

            writer = new PrintWriter(sock.getOutputStream());

        } catch (Exception e ) {
            System.out.println("Error in setNet");
        }
    }

    public static class Listener implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    textArea.append(message + "\n");
                }
            } catch (Exception e) {

            }

        }
    }
}
