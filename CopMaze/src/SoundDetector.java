import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * This class represents a sound sensor in the maze.
 */

public class SoundDetector {

    /**
     * soundLevel is the level of sound.
     */
    private double soundLevel;

    /**
     * buffer is for saving audio data.
     */
    private byte[] buffer;

    /**
     * format is for audio format configuration.
     */
    private AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

    /**
     * info is for line information for the microphone.
     */
    private DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

    /**
     * loopthread is a thread for capturing and analyzing audio data.
     */
    private Thread loopThread;

    /**
     * Gets a level of sound.
     * @return the current sound level.
     */
    public double getLevel() {
        return soundLevel;
    }

    /**
     * Starts capturing and analyzing audio at a specified interval.
     * @param intervalMs the interval in milliseconds between audio readings.
     */
    public void start(int intervalMs) {
        loopThread = new Thread(() -> {
            soundDetectLoop(intervalMs);
        });
        loopThread.start();
    }

    /**
     * Stops the audio capture and analysis thread.
     */
    public void stop() {
        loopThread.interrupt();
    }

    /**
     * Continuously captures audio from the microphone, calculates the sound level, and updates
     * the 'soundLevel' property. This method runs in a separate thread.
     *
     * @param intervalMs the interval in milliseconds between audio readings.
     */
    private void soundDetectLoop(int intervalMs) {

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

                Thread.sleep(intervalMs); // Interval between audio readings
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Calculates the RMS of audio samples within a buffer.
     *
     * @param bytesRead The number of bytes read from the audio input.
     * @return The calculated RMS value representing the sound level.
     */
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
