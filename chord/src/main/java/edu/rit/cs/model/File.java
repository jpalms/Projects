package edu.rit.cs.model;

import java.io.Serializable;

public class File implements Serializable {

    private String path;
    private String fileContent, fileName;

    public File(String filePath){
        this.path = filePath;
    }

    public String getPath() {
        return path;
    }

    public String getFileName(){
        return fileName;
    }

    public String getFileContent(){
        return fileContent;
    }
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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
