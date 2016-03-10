public class DesCounter{
	public static void main(String[] args){
		if(args.length != 4){
			System.out.println("Incorrect # of args. Usage: java DesCounter <inputfile> <outputfile> <1/0>");
			System.exit(0);
		}
		String inputFileName = args[1];
		String outputFileName = args[2];
		int flag = Integer.parseInt(args[3]); //1 for encryption, 0 for decryption
		
	}
}
