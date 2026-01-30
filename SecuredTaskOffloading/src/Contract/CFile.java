package Contract;

import java.io.Serializable;

/**
 * Represents a file to be transferred between client and server
 * Used for uploading task class files
 */
public class CFile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // File name (e.g., "FactorizationTask.class")
    private String fileName;
    
    // File content as byte array
    private byte[] fileContent;
    
    /**
     * Constructor
     */
    public CFile(String fileName, byte[] fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }
    
    // Getters and Setters
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public byte[] getFileContent() {
        return fileContent;
    }
    
    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
    
    /**
     * Get file size in bytes
     */
    public int getFileSize() {
        return fileContent != null ? fileContent.length : 0;
    }
    
    @Override
    public String toString() {
        return "CFile{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + getFileSize() + " bytes" +
                '}';
    }
}
