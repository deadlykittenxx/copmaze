import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SoundDetector {
    double soundLevel;
    byte[] buffer;
    AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

    public void soundDetectStart() {

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Microphone not supported.");
            return;
        }

        try {
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            buffer = new byte[4096];

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                soundLevel = getSoundLevel(bytesRead);
                // System.out.println("Sound level: " + soundLevel);

                Thread.sleep(1000); // Interval between audio readings
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private double getSoundLevel(int bytesRead) {
        double rms = 0.0;
        for (int i = 0; i < bytesRead; i += 2) {
            short sample = (short)((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
            rms += sample * sample;
        }
        rms = Math.sqrt(rms / bytesRead);

        return rms;
    }





}
