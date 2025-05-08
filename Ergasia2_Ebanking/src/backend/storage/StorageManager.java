package backend.storage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StorageManager {
  private static StorageManager instance;
  private String storagePath;

  private StorageManager() {
    this.storagePath = ".data/"; 
  }

  public static StorageManager getInstance() {
    if (instance == null) {
      instance = new StorageManager();
    }
    return instance;
  }

  public String getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(String storagePath) {
    this.storagePath = storagePath;
  }

  public void save(Storable storable, String fileName) throws IOException {
	    String filePath = storagePath + fileName;
	    FileWriter writer =null;
	    try {
	      File file = new File(filePath);
	      if (!file.exists()) {
	        file.createNewFile();
	      }
	      writer = new FileWriter(file);

	      writer.write(storable.marshal());
	    } finally {
	    	try {
	    		writer.close();    		
	    	}catch(IOException ex) {
	    		
	    	}
	    }
	  }

  public void load(Storable storable, String fileName)
      throws  IOException, FileNotFoundException {
    String filePath = storagePath + fileName;
    File file = new File(filePath);
    if (!file.exists()) {
      throw new FileNotFoundException("File " + filePath + " does not exist");
    }
    if (!file.canRead() || !file.isFile()) {
      throw new IOException("File " + filePath + " cannot be read");
    }
    BufferedReader fileBufferReader = null;
    try {
      fileBufferReader = new BufferedReader(new FileReader(file));
      String line;
      StringBuffer sb = new StringBuffer("");
      while ((line = fileBufferReader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      storable.unmarshal(sb.toString());
    } finally {
      try {
        fileBufferReader.close();
      } catch (IOException e) {
      }
    }
  }
}