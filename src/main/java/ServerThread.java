/**
 * Created by dic on 18-09-2015.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ServerThread extends Thread
{  private Server server    = null;
    private Socket           socket    = null;
    private int              ID        = -1;
    private DataInputStream  streamIn  =  null;
    private DataOutputStream streamOut = null;
    private InetAddress           ip;
    private String FILE_TO_BE_RECEIVED = "C:\\Users\\dic\\sent\\file.mp3";
    private String PATH = "C:\\Users\\dic\\sent\\";
    private int imageCounter= 1;
    public  int FILE_SIZE = 53291009;
    public int repeted= 0;
    public ServerThread(Server _server, Socket _socket)
    {  super();
        server = _server;
        socket = _socket;
        ID     = socket.getPort();
    }
    public void send(String msg)
    {   try
    {  streamOut.writeUTF(msg);

    }
    catch(IOException ioe)
    {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
        server.remove(ID);
        stop();
    }
        finally
        {
            try {
                streamOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getID()
    {  return ID;
    }

    public void setIp(InetAddress ip)
    {
        this.ip = ip;
    }

    public InetAddress getIp()
    {
        return ip;
    }

    public void run()
    {  System.out.println("Server Thread " + ID + " running.");
        while (true)
        {  try
        {  server.handle(ID, streamIn.readUTF());

        }
        catch(IOException ioe)
        {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
        }
    }
    public void open() throws IOException
    {  streamIn = new DataInputStream(new
            BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new
                BufferedOutputStream(socket.getOutputStream()));
    }
    public void close() throws IOException
    {  if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }

    public void receiveFile()
    {

        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
                InputStream is = socket.getInputStream();
                String IMAGE_TO_BE_RECEIVED = PATH + "file " + imageCounter++ + "-"+ getID() +  ".IIQ";
                fos = new FileOutputStream(IMAGE_TO_BE_RECEIVED);
                bos = new BufferedOutputStream(fos);
                int sizeReceived = 0;
                int bytesRead = 8192;
                byte[] buffer = new byte[bytesRead];
                while(sizeReceived<FILE_SIZE && (bytesRead = is.read(buffer, 0, 8192))>0)
                {
                    sizeReceived += bytesRead;
                    System.out.println(sizeReceived + " Available: " + is.available() + "Count: " + bytesRead);
                    bos.write(buffer, 0, bytesRead);
                    bos.flush();
                }
                System.out.println("File " + IMAGE_TO_BE_RECEIVED
                        + " downloaded (" + sizeReceived + " bytes read)" + " repeted:  " + repeted);


            if (imageCounter==999) {
                imageCounter = 0;
                repeted++;
            }
                if (imageCounter<1000)
            {
                send("server:" + "send");
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                if (bos != null) bos.close();
                if (fos != null) fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}