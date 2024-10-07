import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;/**
 * A simple network client-server pair
 * @http://stackoverflow.com/questions/3245805
 */
public class echo implements ActionListener, Runnable {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;
    private final JFrame f = new JFrame();
    private final JTextField tf = new JTextField(25);
    private final JTextArea ta = new JTextArea(15, 25);
    private final JButton send = new JButton("Send");
    private volatile PrintWriter out;
    private Scanner in;
    private Thread thread;
    private Kind kind;


    public static enum Kind {

        Client(100, "Trying"), Server(500, "Awaiting");
        private int offset;
        private String activity;

        private Kind(int offset, String activity) {
            this.offset = offset;
            this.activity = activity;
        }
    }

    public echo(Kind kind) throws ClassNotFoundException {
        this.kind = kind;
        f.setTitle("Echo " + kind);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getRootPane().setDefaultButton(send);
        f.add(tf, BorderLayout.NORTH);
        f.add(new JScrollPane(ta), BorderLayout.CENTER);
        f.add(send, BorderLayout.SOUTH);
        f.setLocation(kind.offset, 300);
        f.pack();
        send.addActionListener(this);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        display(kind.activity + HOST + " on port " + PORT);
        thread = new Thread(this, kind.toString());
    }

    public String UserUsername;

    public void start() {
        f.setVisible(true);
        thread.start();
    }

    //@Override
    public void actionPerformed(ActionEvent ae) {
        String s = tf.getText();
        if (out != null) {
            out.println(s);
        }
        try {
            display(s);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        tf.setText("");
    }

    //@Override
    public void run() {
        try {
            Socket socket;
            if (kind == Kind.Client) {
                socket = new Socket(HOST, PORT);
            } else {
                ServerSocket ss = new ServerSocket(PORT);
                socket = ss.accept();
            }
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            display("Connected");
            while (true) {
                display(in.nextLine());
            }
        } catch (Exception e) {
            try {
                display(e.getMessage());
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace(System.err);
        }
    }

    private void display(final String s) throws ClassNotFoundException {
        EventQueue.invokeLater(new Runnable() {
            //@Override
            public void run() {
                ta.append(s + "\u23CE\n");
            }

        });
        insertData(s);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            //@Override
            public void run() {
                try {
                    new echo(Kind.Server).start();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    new echo(Kind.Client).start();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    void insertData(String What) throws ClassNotFoundException {
        boolean flag;
        flag = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/jdbcdemo", "root", ""
            );
            Statement statement = connection.createStatement();
            int count = 0;


            int result = statement.executeUpdate(
                    "insert into history values('" + What + "')");

            // if result is greater than 0, it means values
            // has been added
            if (result > 0)
                System.out.println("successfully inserted");

            else
                System.out.println(
                        "unsucessful insertion ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

