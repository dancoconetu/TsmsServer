package Common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dic on 08-10-2015.
 */
public class FolderInfo {

    public File folderPath;
    public SystemInfo systemInfo;
    public FolderInfo(SystemInfo systemInfo)
    {
        this.systemInfo = new SystemInfo();
         folderPath = systemInfo.getPathForHome();

    }
    //Common.SystemInfo systemInfo = new Common.SystemInfo();

    public File[] getAllFolderFiles()
    {
        File folder = folderPath;
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
            if (getFileExtension(listOfFiles[i]).equals("IIQ"))
            {
                System.out.println("This is an image");
            }
        }
        return listOfFiles;
    }

    public ArrayList<File> getAllFilesWithExtension(String extension)
    {   //folderPath = systemInfo.getPathForHome();
        File folder = folderPath;
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> allFilesWithExtension = new ArrayList<File>();

        for (int i = 0; i < listOfFiles.length; i++)
        if (getFileExtension(listOfFiles[i]).equals(extension))
        {
            allFilesWithExtension.add( listOfFiles[i]);

        }
        return allFilesWithExtension;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public void createFolder(String name)
    {
        //System.out.println("Folder created: " +( folderPath.getCanonicalPath() ) );
        try {
            File f = new File(folderPath.getCanonicalPath()+ "/" + name + "/" + name);
            System.out.println(f.mkdirs() + " : " + f.getCanonicalFile());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public ArrayList<File> getOnlyFiles(File file)
    {   //folderPath = systemInfo.getPathForHome();
        File folder = file;
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> onlyFiles = new ArrayList<File>();

        for (File f: listOfFiles)
            if (f.isFile() && !f.isHidden())
            {
                onlyFiles.add(f);

            }
        return onlyFiles;
    }


    public boolean deleteFiles(ArrayList<File> files)
    {   boolean t= false;
        for (File f : files)
        {
             t= f.delete();
        }

        return t;
    }

    public ArrayList<File> getFolders(File file)
    {
        File folder = file;
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> onlyFolders = new ArrayList<File>();

        for (File f: listOfFiles)
            if (f.isDirectory() && !f.isHidden())
            {
                onlyFolders.add(f);

            }
        return onlyFolders;
    }

    public void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

}
