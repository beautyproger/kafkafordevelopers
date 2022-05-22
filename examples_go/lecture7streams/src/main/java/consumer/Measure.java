package consumer;

import java.io.Serializable;

public class Measure implements Serializable {
    public String timestamp;
    public String device;
    public String signal;
    public String temperature;
    public String units;

    public Measure() {

    }
    public Measure(String timestamp, String device, String signal, String temperature, String units) {
        this.timestamp = timestamp;
        this.device = device;
        this.signal = signal;
        this.temperature = temperature;
        this.units = units;
    }
}
