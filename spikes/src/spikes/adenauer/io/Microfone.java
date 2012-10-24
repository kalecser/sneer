package spikes.adenauer.io;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


class Microfone implements Runnable {
	 String errStr;
    TargetDataLine line;
    Thread thread;
    double   duration = 0;
    AudioInputStream audioInputStream;
    
    public void start() {
        errStr = null;
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop() {
        thread = null;
    }
    
    private void shutDown(String message) {
        if ((errStr = message) != null && thread != null) {
            thread = null;
            System.err.println(errStr);
        }
    }

    @Override public void run() {
        // define the required attributes for our line, and make sure a compatible line is supported.
    	//sampleRate = 44100 (860.100), 22050 (430.666), 16000 (315.015). Tested with 10 seconds of time.
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 2, 4 , 16000, false );
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    
        if (!AudioSystem.isLineSupported(info)) {
            shutDown("Line matching " + info + " not supported.");
            return;
        }

        // get and open the target data line for capture.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (LineUnavailableException ex) { 
            shutDown("Unable to open the line: " + ex);
            return;
        } catch (SecurityException ex) { 
            shutDown(ex.toString());
            //JavaSound.showInfoDialog();
            return;
        } catch (Exception ex) { 
            shutDown(ex.toString());
            return;
        }

        // play back the captured audio data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead = 0;
        line.start();

        while (thread != null) {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }
            out.write(data, 0, numBytesRead);
        }

        // we reached the end of the stream.  stop and close the line.
        line.stop();
        line.close();
        line = null;
    
        print(out.toByteArray());
        print(out.size());
    }

    private void print(int size) {
		System.out.println();
    	System.out.print("size : " + size );
	}

	private void print(byte[] data) {
    	for ( byte b : data)
    		if (b == 0)
    			System.out.println(b);
    		else
    			System.out.print(b + ", ");
    }
    
    public static void main(String[] ignored) {
    	Microfone m = new Microfone();
    	m.start();
    	try {
    		Thread.sleep(1000*5);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
    	m.stop();
    }
}