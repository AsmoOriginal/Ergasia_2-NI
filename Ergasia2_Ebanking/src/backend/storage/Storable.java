package backend.storage;


public interface Storable {
String marshal();
void unmarshal(String data);

}
