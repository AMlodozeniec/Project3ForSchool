//package com.tomcov.server;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cryptage {
	
    
    public Cryptage(){
    }

	public byte[] Encrypt (String Pass) throws Exception{

		byte[] plainText = Pass.getBytes();

		byte[] raw = new byte[]{0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55};
		byte[] keyBytes = addParity(raw);
		SecretKey key = new SecretKeySpec(keyBytes, "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plainText);

		return cipherText;
	}

	public String Decrypt (byte[] cipherText) throws Exception{

		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		byte[] raw = new byte[]{0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55};
		byte[] keyBytes = addParity(raw);
		SecretKey key = new SecretKeySpec(keyBytes, "DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] newPlainText = cipher.doFinal(cipherText);

		return new String(newPlainText, "UTF8");
	}

	public static byte[] addParity(byte[] in) {
		byte[] result = new byte[8];

		// Keeps track of the bit position in the result
		int resultIx = 1;

		// Used to keep track of the number of 1 bits in each 7-bit chunk
		int bitCount = 0;

		// Process each of the 56 bits
		for (int i=0; i<56; i++) {
			// Get the bit at bit position i
			boolean bit = (in[6-i/8]&(1<<(i%8))) > 0;

			// If set, set the corresponding bit in the result
			if (bit) {
				result[7-resultIx/8] |= (1<<(resultIx%8))&0xFF;
				bitCount++;
			}

			// Set the parity bit after every 7 bits
			if ((i+1) % 7 == 0) {
				if (bitCount % 2 == 0) {
					// Set low-order bit (parity bit) if bit count is even
					result[7-resultIx/8] |= 1;
				}
				resultIx++;
				bitCount = 0;
			}
			resultIx++;
		}
		return result;
	}

}
