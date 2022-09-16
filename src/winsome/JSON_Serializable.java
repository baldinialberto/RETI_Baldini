package winsome;

import java.io.IOException;

public interface JSON_Serializable {
    void JSON_write(String filePath) throws IOException;
    void JSON_read(String filePath) throws IOException;
}
