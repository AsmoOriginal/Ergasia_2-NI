package backend.storage;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StorageManager {
  private static StorageManager instance;
  private String storagePath;

//Ιδιωτικός constructor για Singleton
  private StorageManager() {
    this.storagePath = "./data/"; 
  }

//Επιστροφή του μοναδικού instance
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

//Αποθήκευση πολλών αντικειμένων Storable
  public void save(List<?> items, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Object item : items) {
                String line = item instanceof Storable ? ((Storable) item).marshal() : item.toString();
                writer.write(line);
                writer.newLine();
            }
        }
    }
  

  // Φόρτωση πολλών γραμμών από αρχείο (ο καθένας αντιστοιχεί σε ένα αντικείμενο)
  public List<String> load(String fileName) throws IOException {
      String filePath = storagePath + fileName;
      File file = new File(filePath);
      if (!file.exists()) {
          throw new FileNotFoundException("File " + filePath + " does not exist");
      }
      if (!file.canRead() || !file.isFile()) {
          throw new IOException("File " + filePath + " cannot be read");
      }

      List<String> lines = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
          String line;
          while ((line = reader.readLine()) != null) {
              if (!line.isBlank()) {
                  lines.add(line);
              }
          }
      }
      return lines;
  }
}