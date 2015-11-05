/**
 * Created by dic on 18-09-2015.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MasterThread extends Thread
{  private Master master = null;
    private Socket           socket    = null;
    private int              ID        = -1;
    private DataInputStream  streamIn  =  null;
    private DataOutputStream streamOut = null;
    private InetAddress           ip;
    private String PATH = "C:\\Users\\dic\\sent\\";
    private String PATH2 = "C:\\Users\\dic\\ToSend\\";
    public int repeted= 0;
    private byte[] mybytearray;
    public MasterThread(Master _master, Socket _socket)
    {  super();
        master = _master;
        socket = _socket;
        ID     = socket.getPort();
    }
    public void send(String msg)
    {
        try

        {
            streamOut.writeUTF(msg);
        }
        catch(IOException ioe)
        {
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            master.remove(ID);
            stop();
        }
        finally
        {
            try
            {
                streamOut.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public int getID()
    {
        return ID;
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
    {
        System.out.println("Master Thread " + ID + " running.");
        while (true)
        {
            try
            {
            master.handle(ID, streamIn.readUTF());
            }
            catch(IOException ioe)
            {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                master.remove(ID);
                stop();
            }
        }
    }

    public void open() throws IOException
    {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void close() throws IOException
    {   if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }

    public void receiveFile()
    {   master.inUse = true;
        try
        {
            sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try
        {   send("Go");
            long startTime = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            DataInputStream dis = new DataInputStream(bis);
            String imageName = dis.readUTF();
            String imageFound = dis.readUTF();
            System.out.println(imageFound);
            if (!imageFound.equals("ImageFound") || imageFound.equals("ImageNotFound")  )
            {
                throw new Exception();
            }
            String IMAGE_TO_BE_RECEIVED = PATH + imageName ;
            fos = new FileOutputStream(IMAGE_TO_BE_RECEIVED);
            bos = new BufferedOutputStream(fos);
            long fileSize = dis.readLong();
            System.out.println("File size: " + fileSize);
            int sizeReceived = 0;
            int bytesRead = 8192;
            byte[] buffer = new byte[bytesRead];
            while(sizeReceived<fileSize && (bytesRead = bis.read(buffer, 0, 8192))>0)
                {
                    sizeReceived += bytesRead;
                    //System.out.println(sizeReceived + " Available: " + bis.available() + "Count: " + bytesRead);
                    bos.write(buffer, 0, bytesRead);
                    bos.flush();
                }
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("File " + IMAGE_TO_BE_RECEIVED + " downloaded (" + sizeReceived + " bytes read)"
                                       + " repeated:  " + repeted + " Time Elapsed: " + estimatedTime/1000.0 );
            if (fileSize != sizeReceived )
            System.out.println("malicious file sent");


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (bos != null) bos.close();
                if (fos != null) fos.close();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        master.inUse = false;
    }


    public void sendFile(File myFile) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        //OutputStream os = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos;

        //send("sendToClient");
        try
        {
            bos = new BufferedOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        dos = new DataOutputStream(bos);
        sleepTime();

        try
        {
            DataInputStream streamIn  = new DataInputStream(socket.getInputStream());
            System.out.println("waiting for Goooooooooooooooooooooooooooo");
           // while (!streamIn.readUTF().equals("Go")){}
            System.out.println("It shoiuld gooooo");
                    send(myFile.getName());
           // File myFile = new File(imagePath);

            mybytearray = new byte[(int) myFile.length()];

            fis = new FileInputStream(myFile);


            bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            if(mybytearray.length > 0) {
                send("ImageFound");

                long fileLength = myFile.length();
                dos.writeLong(fileLength);

                System.out.println("Sending " + myFile.getCanonicalPath() + "(" + mybytearray.length + " bytes)");
                bos.write(mybytearray, 0, mybytearray.length);
                bos.flush();
            }
            else
            send("ImageNotFound");
            System.out.println("Done.");


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found!");
            send("ImageNotFound");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("sent");
            sleepTime();

            //sendMessage("succesfully sent");
//             try {
//                 if (bis != null) bis.close();
//
//
//             } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }

    public void sleepTime()
    {
        try
        {
            sleep(100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}