/**
 * Created by dic on 18-09-2015.
 */

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Master implements Runnable
{  private MasterThread clients[] = new MasterThread[50];
    private ServerSocket server = null;
    private Thread       thread = null;
    private int clientCount = 0;
    private DataInputStream  console   = null;
    public boolean inUse= false;
    private Queue<String> queue = new LinkedList<String>();
    private Mutex mutex = new Mutex();

    public Master(int port)
    {  try
    {  System.out.println("Binding to port " + port + ", please wait  ...");
        server = new ServerSocket(port);
        System.out.println("Master started: " + server);
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
                        clients[i].sendFile(new File("C:\\Users\\dic\\IdeaProjects\\Tsms-Client\\target\\tsms-client-1.0-SNAPSHOT-jar-with-dependencies.jar"));
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
    {  System.out.println("Master accept error: " + ioe); stop(); }

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
    {   System.out.println("Input + " + ID + ":  " + input);
        String s = ID + "";
        queue.add(s);
        if (input.equals(".bye"))
    {  clients[findClient(ID)].send(".bye");
        remove(ID); }

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
        {  MasterThread toTerminate = clients[pos];
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

        clients[clientCount] = new MasterThread(this, socket);
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
    {  Master master = null;
       // if (args.length != 1)
        //    System.out.println("Usage: java Master port");
       // else
            master = new Master(7777);
    }
}
