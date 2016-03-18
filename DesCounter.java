import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

public class DesCounter{
	String counter = "00001234";
	byte[] raw = new byte[]{0x01,0x72,0x43,0x3E,0x1C,0x7A,0x55};
	String inputFileName;
	String outputFileName;
	int flag;
	FileReader reader;
	BufferedReader bufferInput;
	File outputFile;
	Writer writer;
	BufferedWriter bufferOutput;
		
	public void checkArgs(String[] args){
		if(args.length != 3){
			System.out.println("Incorrect # of args. Usage: java DesCounter <input_file> <output_file_name> <1/0>");
			System.exit(0);
		}
		inputFileName = args[0];
		outputFileName = args[1];
		flag = Integer.parseInt(args[2]); //1 for encryption, 0 for decryption
		if(flag != 0 && flag != 1){
			System.out.println("The 3rd argument must be either a '1' or a '0'");
			System.exit(0);
		}
		
	}
	
	public ArrayList<String> splitStringIntoBlocks(String line){
		ArrayList<String> array = new ArrayList<String>();
		int numBlocks = 0;
		int arrayIndex = 0;
		while(line.length() > 0){
			if(line.length() >= 8){
				String chunk = line.substring(0,8);
				//System.out.println(chunk);
				array.add(arrayIndex, chunk); //Add to arraylist
				arrayIndex++;
				line = line.substring(8,line.length());
			}
			else{
				String chunk = line.substring(0,line.length());
				//System.out.println(chunk);
				array.add(arrayIndex, chunk);
				line = line.substring(line.length(),line.length());
			}
			numBlocks++;
		}
		System.out.println("Number of plaintext blocks: " + numBlocks);
		return array;
	}
	
	public String updateCounter(){
		int counterInt = Integer.parseInt(counter.toString());
		counterInt++;
		String s = String.format("%08d", counterInt);
		//System.out.println("Updatedcounter is " + s);
		return s;
	}
	
	public void encrypt(ArrayList<String> charBlock, DesEncrypter encrypter) throws Exception{
		StringBuilder sb = new StringBuilder();
		for(String block: charBlock){
				String eCounter = encrypter.encrypt(counter);		
				String s = xor(block, eCounter,1);
				sb.append(s);
				counter = updateCounter();
		}
		String output = sb.toString();
		bufferOutput.write(output); 
		bufferOutput.close();
	}
	
	public void decrypt(ArrayList<String> charBlock, DesEncrypter decrypter) throws Exception{
		StringBuilder sb = new StringBuilder();
		for(String block: charBlock){
				String eCounter = decrypter.encrypt(counter);		
				String s = xor(block, eCounter, 0);
				sb.append(s);
				counter = updateCounter();
		}
		String output = sb.toString();
		//System.out.println("Number of characters in outputfile: " + output.length());
		bufferOutput.write(output); 
		bufferOutput.close();
	}
	
	public void printContents(ArrayList<String> array){
		for(String s: array){
			System.out.println("Contents of chunk: " + s);
		}
	}	
	
	public String xor(String s, String counter, int f) throws Exception{
		StringBuilder sb = new StringBuilder();
		BASE64Encoder enc = new BASE64Encoder();
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] sBytes = s.getBytes();
		
		byte[] counterBytes = counter.getBytes();
		
		byte[] out = new byte[sBytes.length];
		
	//	System.out.println("counter is " + counter);
	//	System.out.println("sBytes length " + sBytes.length + " counterlength " + counterBytes.length);
		for(int i = 0; i < sBytes.length; i++){
			out[i] = (byte) (sBytes[i] ^ counterBytes[i % counterBytes.length]);	
			//sb.append((char)(s.charAt(i) ^ counter.charAt(i % counter.length())));
		}
		if(f == 1){
			String result = enc.encode(out).replaceAll("\\s", "");
			sb.append(result);
		//String result = sb.toString();
		//	System.out.println("xored string: " + result);
			return result;
		}
		else{
			String result = new String(out);
		//	System.out.println("xored string: " + result);
			return result;
		}
		
	}	
	
	public SecretKey createSecretKey(){
		Cryptage cipher = new Cryptage();
		
		byte[] keyBytes = cipher.addParity(raw);
		SecretKey key = new SecretKeySpec(keyBytes, "DES");
		return key;
	}	
	
	public String readFile() throws Exception{
		reader = new FileReader(inputFileName);
		bufferInput = new BufferedReader(reader);
		String line = bufferInput.readLine();
		return line;
	}
	
	public void openWriteBuffer() throws Exception{
		outputFile = new File(outputFileName);
		writer = new FileWriter(outputFile);
		bufferOutput = new BufferedWriter(writer);
	
	}
	
	public static void main(String[] args) throws Exception{
		DesCounter d = new DesCounter();
		d.checkArgs(args);
		//System.out.format("input: %s output: %s flag: %d\n", d.inputFileName, d.outputFileName, d.flag);
		
		String line = d.readFile();
		System.out.println("Number of characters in inputfile: " + line.length());
		d.openWriteBuffer();
		
		SecretKey key = d.createSecretKey();
		DesEncrypter encrypter = new DesEncrypter(key);
		
		
		if(d.flag == 1){ //Encryption
			ArrayList<String> charBlock = d.splitStringIntoBlocks(line);
			//d.printContents(charBlock);
			d.encrypt(charBlock, encrypter);
		}
		else if(d.flag == 0){//Decryption
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] sBytes = decoder.decodeBuffer(line);
			String decodedLine = new String(sBytes);
			ArrayList<String> charBlock = d.splitStringIntoBlocks(decodedLine);
			d.decrypt(charBlock, encrypter);	
		}
	}
}
