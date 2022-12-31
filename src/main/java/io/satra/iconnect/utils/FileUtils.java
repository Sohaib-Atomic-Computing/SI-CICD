package io.satra.iconnect.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtils {

    /**
     * This method is used to take the module name, file name, and the entity id and build the file path then returns it
     *
     * @param moduleName - module name
     * @param fileName - file name
     * @return file path
     */
    public String rename(String moduleName, String fileName) {
        synchronized (FileUtils.class) {
            if (fileName == null || fileName.equals("")) {
                return null;
            }
            String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            fileName = (moduleName + "_" + fileName.substring(0, fileName.lastIndexOf(".")) + "_" + String.valueOf(new Date().getTime()).concat(fileExtension));
        }

        return fileName;
    }

    /**
     * This method is used to take the multipart files and the entity id then save the files on the system and returns the files paths
     *
     * @param files - multipart files
     * @param entityId - entity id
     * @return true if file deleted else false
     */
    public String saveFile(List<MultipartFile> files, String entityId) throws IOException {
        FolderUtil folderUtil = new FolderUtil();
        FileUtils fileUtils = new FileUtils();
        ArrayList<String> filesName = new ArrayList<>();
        String filename = "";

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; //next pls
            }
            filename = fileUtils.rename("IConnect", file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folderUtil.getPath(entityId) + filename);
            Files.write(path, bytes);
            filesName.add(entityId + "/" + filename);
        }

        return filesName.isEmpty() ? null : String.join(",", filesName);
    }

    /**
     * This method is used to take the file path and delete the file from the system
     *
     * @param filePath - file path
     * @throws IOException if the file does not exist
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(PropertyLoader.getPathStorage() + filePath);
        // if the file exist then delete it
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}
