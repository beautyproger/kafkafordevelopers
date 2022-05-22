package generator;

import consumer.Measure;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    public List<Measure> readCSV() {
        List<Measure> records = new ArrayList<>();
        URL resourceUrl = getClass().getClassLoader().getResource("data.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(new File(resourceUrl.toURI())))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(new Measure(
                    values[0],
                    values[1],
                    values[2],
                    values[3],
                    values[4]
                ));
            }
            return records;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
