/**
 * Created by dic on 18-09-2015.
 */

import Common.FolderInfo;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

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
    private String PATH = "C:\\Users\\dic\\sent";
    private Mutex mutex;

    public int repeted= 0;
    private byte[] mybytearray;
    public MasterThread(Master _master, Socket _socket, Mutex _mutex)
    {  super();
        master = _master;
        socket = _socket;
        ID     = socket.getPort();
        mutex = _mutex;
    }
    public void sendMessage(String _msg) {
        final String msg = _msg;
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try

                {   System.out.println("Aquiring in SENDMESSAGE");
                    mutex.acquire();
                    streamOut.writeUTF(msg);
                    System.out.println("ACQUIRED in SENDMESSAGE");
                    mutex.release();
                } catch (IOException ioe) {
                    System.out.println(ID + " ERROR sending: " + ioe.getMessage());
                    master.remove(ID);
                    stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        streamOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();
    }

    public void sendMessageWithoutMutex(String message)
    {
        try {
            streamOut.writeUTF(message);
            streamOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
    {

        try
        {   System.out.println("Try to acquire sendMutex receive");
            master.receiveMutex.acquire();
            //mutex.acquire();
            System.out.println("Mutex acquired");
            sleep(100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        long sizeReceived = 0;
         long fileSize = 0;
        String IMAGE_TO_BE_RECEIVED="";
        try
        {   //sendMessage("Go");
            long startTime = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            DataInputStream dis = new DataInputStream(bis);
            String imageName = dis.readUTF();
            String imagePath = dis.readUTF();
            File path2 =  new File(PATH + imagePath);
            path2.mkdirs();
            String imageFound = dis.readUTF();
            System.out.println(imageFound);
            if (!imageFound.equals("ImageFound") || imageFound.equals("ImageNotFound")  )
            {
                throw new Exception();
            }
            IMAGE_TO_BE_RECEIVED = path2.getCanonicalPath() + File.separator + imageName ;
            fos = new FileOutputStream(IMAGE_TO_BE_RECEIVED);
            bos = new BufferedOutputStream(fos);
            fileSize = dis.readLong();
            System.out.println("File size: " + fileSize);

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
                if (fileSize != sizeReceived ) {
                    System.out.println("malicious file sent");
                    new File(IMAGE_TO_BE_RECEIVED).delete();

                }



            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        master.receiveMutex.release();
        System.out.println("Mutex released Receive");
        master.inUse = false;
    }


    public void sendMultipleFiles(File folder, FolderInfo folderInfo)
    {
        for (File f: folderInfo.getOnlyFiles(folder))
        {

            sendFile(f,folderInfo);
            System.out.println(f.getName() + " path from TSMS: " + f.getAbsolutePath().substring( folderInfo.folderPath.getAbsolutePath().length()));

        }

        for (File f : folderInfo.getFolders(folder))
        {
            System.out.println(f.getName() + ": " + f.getAbsolutePath().substring( folderInfo.folderPath.getAbsolutePath().length()));
            sendMultipleFiles(f,folderInfo);
        }

    }


    public void sendFile(File myFile, FolderInfo folderInfo) {

        try {
            System.out.println("Try to acquire sendMutex send");
            mutex.acquire();
            System.out.println("Mutex acquired send");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        //OutputStream os = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos;
        sendMessageWithoutMutex("server:sendToClient");

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
        {   System.out.println("Sending " + myFile.getCanonicalPath() + "(" + myFile.length() + " bytes)");

            DataInputStream streamIn  = new DataInputStream(socket.getInputStream());
            //while (!streamIn.readUTF().equals("Go")){}
            if (myFile.length()> 150502457)
                throw new FileNotFoundException();
            sendMessageWithoutMutex(myFile.getName()); //sending file name
            sendMessageWithoutMutex(myFile.getParentFile().getAbsolutePath().substring(folderInfo.folderPath.getAbsolutePath().length()));

            mybytearray = new byte[(int) myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);

            bis.read(mybytearray, 0, mybytearray.length);
            sendMessageWithoutMutex("ImageFound");

            long fileLength = myFile.length();
            dos.writeLong(fileLength);

            bos.write(mybytearray, 0, mybytearray.length);
            bos.flush();
            System.out.println("Done.");
            mutex.release();
            System.out.println("Mutex released SEND");


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.out.println("File not found!");
            sendMessageWithoutMutex("ImageNotFound");

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
        try {
            sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}