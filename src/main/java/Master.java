/**
 * Created by dic on 18-09-2015.
 */

import Common.FolderInfo;
import Common.SystemInfo;
import Common.XMLClasses.XMLCreator;
import Common.XMLClasses.XMLParser;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
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
    public Mutex sendMutex = new Mutex();
    public Mutex receiveMutex = new Mutex();
    SystemInfo systemInfo = new SystemInfo();
    private Mutex taskMutex = new Mutex();

    private String PATH2 = "C:\\Users\\dic\\ToSend\\";
    FolderInfo folderInfo;

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

            while (true) {
                String s= null;
                try {
                    s = console.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //for (int k=0; k<500; j++)
                for (int i = 0; i < clientCount; i++) {
                    //clients[i].sendMessage("server:" + s);
                    if(s.equals("sendJar"))
                    {   File temp = folderInfo.folderPath;
                        folderInfo.folderPath = new File("C:\\Users\\dic\\IdeaProjects\\Tsms-Client\\target\\");
                        clients[i].sendFile(new File("C:\\Users\\dic\\IdeaProjects\\Tsms-Client\\target\\tsms-client-1.0-SNAPSHOT-jar-with-dependencies.jar"), folderInfo);
                        folderInfo.folderPath = temp;
                    }

                    if (s.equals("1"))

                        clients[i].sendMissingFiles(folderInfo.folderPath, folderInfo);

                    if (s.equals("3"))
                    {
                        XMLCreator xmlCreator = new XMLCreator(folderInfo);
                        String sendFileXml = xmlCreator.createSendFileXMLDoc(new File("C:\\Users\\dic\\ToSend\\DSDSD\\Haleluia\\Haleluia\\K-3 II\\DCIM\\100_0702\\_IMG3404.JPG"));
                        System.out.println(sendFileXml);
                        String sendMultipleFilesXML = xmlCreator.createSendMultipleFilesXml(folderInfo.getOnlyFiles(folderInfo.folderPath));
                        System.out.println(sendMultipleFilesXML);
                        String scriptXml =  xmlCreator.createScriptRunningXML(new File("C:\\Users\\dic\\ToSend\\DSDSD\\Haleluia\\Haleluia\\K-3 II\\hello.py")
                                , "C:\\Python34\\python.exe", "py");
                        System.out.println(scriptXml);
                        ArrayList<String> list = new ArrayList<String>();
                        list.add("C:\\A");
                        list.add("C:\\B");
                        list.add("C:\\C");
                        list.add("C:\\D");
                        list.add("C:\\E");
                        list.add("C:\\F");
                        list.add("C:\\G");
                        list.add("C:\\H");
                        list.add("C:\\I");
                        String osInfoXml = xmlCreator.sendOsInfo(list, "testPc", "mac");
                        System.out.println(osInfoXml);

                        XMLParser xmlParser = new XMLParser();
                        xmlParser.parseSendMultipleFiles(sendMultipleFilesXML);
                        xmlParser.parseSendFile(sendFileXml);
                        System.out.println(xmlParser.parseScript(scriptXml).get("ScriptVersion"));
                        Hashtable hashtable = xmlParser.parseOsInfo(osInfoXml);
                        System.out.println("OsName:" + hashtable.get("OsName"));
                        System.out.println("PcName:" + hashtable.get("PcName"));
                        String[] scriptLanguages = (String[])hashtable.get("ScriptLanguages");
                        System.out.println("Script lagnuages size: " + scriptLanguages.length);
                        for (int j=0; j< scriptLanguages.length; j++)
                        {
                            System.out.println("language: " + scriptLanguages[i]);
                        }



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
        systemInfo.setPathForHome(new File( PATH2));
        folderInfo  = new FolderInfo(systemInfo);
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
    {    XMLParser xmlParser = new XMLParser();
        System.out.println("Input + " + ID + ":  " + input);
        String s = ID + "";
        queue.add(s);
        if (input.equals(".bye"))
    {  clients[findClient(ID)].sendMessage(".bye");
        remove(ID); }

        if (input.length()>=10)
        if(input.contains("<SendFile")) {

            System.out.println("Trying to receive");
            Hashtable hashtable = xmlParser.parseSendFile(input);
            clients[findClient(ID)].receiveFile(hashtable.get("FileName").toString(), hashtable.get("FilePath").toString(), Long.parseLong(hashtable.get("FileSize").toString()));




        }
        if(input.contains("<SendMultipleFiles")) {


            clients[findClient(ID)].sendMultipleFilesFromList(xmlParser.parseSendMultipleFiles(input));
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

        clients[clientCount] = new MasterThread(this, socket, sendMutex,folderInfo);
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
