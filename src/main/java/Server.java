/**
 * Created by dic on 18-09-2015.
 */

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server implements Runnable
{  private ServerThread clients[] = new ServerThread[50];
    private ServerSocket server = null;
    private Thread       thread = null;
    private int clientCount = 0;
    private DataInputStream  console   = null;
    public boolean inUse= false;
    private Queue<String> queue = new LinkedList<String>();
    private Mutex mutex = new Mutex();

    public Server(int port)
    {  try
    {  System.out.println("Binding to port " + port + ", please wait  ...");
        server = new ServerSocket(port);
        System.out.println("Server started: " + server);
        start(); }
    catch(IOException ioe)
    {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
    }
    public void run()

    {  new Thread(){
        public void run() {
            int j=0;

            while (true) {
                String s= null;
                try {
                    s = console.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //for (int k=0; k<500; j++)
                for (int i = 0; i < clientCount; i++) {
                    clients[i].send("server:" + s);
                    if(s.equals("sendToClient"))
                    {
                        clients[i].sendFile("CF000237.IIQ");
                    }

                    if(s.equals("sendJar"))
                    {
                        clients[i].sendFile("CF000237.IIQ");
                    }
                }

            }
        }
    }.start();
        while (thread != null)
    {  try
    {  System.out.println("Waiting for a client ...");
        addThread(server.accept()); }

    catch(IOException ioe)
    {  System.out.println("Server accept error: " + ioe); stop(); }

    }




    }
    public void start()
    {  console   = new DataInputStream(System.in);
        if (thread == null)
    {  thread = new Thread(this);
        BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
        thread.start();
    }
    }
    public void stop()
    {  if (thread != null)
    {  thread.stop();
        thread = null;
    }
    }
    private int findClient(int ID)
    {  for (int i = 0; i < clientCount; i++)
        if (clients[i].getID() == ID)
            return i;
        return -1;
    }
    public synchronized void handle(int ID, String input)
    {   System.out.println("Input: " + input);
        String s = ID + "";
        queue.add(s);
        if (input.equals(".bye"))
    {  clients[findClient(ID)].send(".bye");
        remove(ID); }
    //else
       // for (int i = 0; i < clientCount; i++) {
            //if(clients[i].getIp()!= clients[findClient(ID)].getIp())
            //clients[i].send("Ip:" + clients[findClient(ID)].getIp() + " Thread:" + ID + ">> " + input);

        //}

           // System.out.println("Ip:" + clients[findClient(ID)].getIp() + " Thread:" +  ID + ">> " + input);
        if (input.length()>=10)
        if(input.substring(0,10).equals("Sending..."))
        {

            System.out.println("Trying to receive");
            try {
                mutex.acquire();
                clients[findClient(ID)].receiveFile();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                mutex.release();
            }

        }
    }
    public synchronized void remove(int ID)
    {  int pos = findClient(ID);
        if (pos >= 0)
        {  ServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            try
            {  toTerminate.close(); }
            catch(IOException ioe)
            {  System.out.println("Error closing thread: " + ioe); }
            toTerminate.stop(); }
    }
    private void addThread(Socket socket)
    {  if (clientCount < clients.length)
    {  System.out.println("Client accepted: " + socket + " ip:" + socket.getInetAddress());

        clients[clientCount] = new ServerThread(this, socket);
        clients[clientCount].setIp(socket.getInetAddress());
        try
        {  clients[clientCount].open();
            clients[clientCount].start();
            clientCount++; }
        catch(IOException ioe)
        {  System.out.println("Error opening thread: " + ioe); }

    for (int i=0; i<clientCount; i++)
    {
        System.out.println("Client: "  + clients[i].getIp() + ":" + clients[i].getID());
    }
    }
    else
        System.out.println("Client refused: maximum " + clients.length + " reached.");
    }





    public static void main(String args[])
    {  Server server = null;
       // if (args.length != 1)
        //    System.out.println("Usage: java Server port");
       // else
            server = new Server(7777);
    }
}
