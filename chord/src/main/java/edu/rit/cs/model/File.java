package edu.rit.cs.model;

import java.io.Serializable;

/**
 * File class. Used to represent our Chord (key, value) storage.
 */
public class File implements Serializable {

    /*
    Class Variables
     */

    private String path;
    private String fileContent, fileName;

    /**
     * File Constructor. Takes in a filepath as an argument
     * @param filePath
     */
    public File(String filePath){
        this.path = filePath;
    }

    //---------------------- Getters -------------------------

    public String getPath() {
        return path;
    }

    public String getFileName(){
        return fileName;
    }

    public String getFileContent(){
        return fileContent;
    }

    //----------------------- Setters -------------------------

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    //---------------------- Overrides ------------------------

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof File))
            return false;
        return this.path.equals(((File) obj).getPath());
    }

    @Override
    public String toString() {
        return "File: " + fileName;
    }

    @Override
    public int hashCode() {
        return fileName.length();
    }
}
