package io.satra.iconnect.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class FolderUtil {
    @Value("${i-connect.app.systemFolderPath}")
    private String systemFolderPath;

    public void createFolder(String folderName) {
        synchronized (FolderUtil.class) {
            try {
                // creating parent folder
                if (isNotFolderExist(systemFolderPath)) {
                    create(systemFolderPath);
                }

                if (isNotFolderExist(systemFolderPath + File.separator + folderName)) {
                    create(systemFolderPath + File.separator + folderName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to take the folder name and creates it then returns the folder path
     *
     * @param folderName folder name
     * @return folder path
     */
    public String getPath(String folderName) {
        createFolder(folderName);
        return (systemFolderPath + folderName + "/");
    }

    /**
     * This method is used to check if the folder exist or not
     *
     * @param fullPath - full path of the folder
     * @return true if folder exist else false
     */
    private boolean isNotFolderExist(String fullPath) {
        File file = new File(fullPath);
        return !file.exists();
    }

    /**
     * This method is used to take the folder full path and creates it on the system
     *
     * @param fullPath - full path of the folder
     * @return true if folder created else false
     */
    private boolean create(String fullPath) {
        File file = new File(fullPath);
        boolean executable = file.setExecutable(true, false);
        boolean readable = file.setReadable(true, false);
        boolean writable = file.setWritable(true, false);
        return file.mkdir() && executable && readable && writable;
    }
}
