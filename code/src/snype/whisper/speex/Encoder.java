package snype.whisper.speex;

public interface Encoder {

	boolean processData(byte[] pcmBuffer);

	byte[] getProcessedData();

}
