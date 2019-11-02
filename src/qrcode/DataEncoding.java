package qrcode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import reedsolomon.ErrorCorrectionEncoding;

public final class DataEncoding {

	private static final Charset CHARSET = StandardCharsets.ISO_8859_1;
	private static final int BYTE_LENGTH = 8;

	private static String padLeft(String input, int length) {
		if (input.length() == length) return input;

		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - input.length()) {
			sb.append("0");
		}

		return sb.append(input).toString();
	}

	/**
	 * @param input The String to convert
	 * @param version The version of the QRCode
	 * @return The binary string of the encoded input
	 */
	public static boolean[] byteModeEncoding(String input, int version) {
		return DataEncoding.bytesToBinaryArray(DataEncoding.encodeString(input, QRCodeInfos.getMaxInputLength((version))));
	}

	/**
	 * @param input
	 *            The string to convert to ISO-8859-1
	 * @param maxLength
	 *          The maximal number of bytes to encode (will depend on the version of the QR code) 
	 * @return A array that represents the input in ISO-8859-1. The output is
	 *         truncated to fit the version capacity
	 */
	public static int[] encodeString(String input, int maxLength) {
		byte[] encodedString = input.getBytes(CHARSET);
		int fixedInputLength = encodedString.length > maxLength ? maxLength : encodedString.length;
		int[] sequence = new int[fixedInputLength];

		for (int index = 0; index < fixedInputLength; index++ ) {
			sequence[index] = Byte.toUnsignedInt(encodedString[index]);
		}

		return sequence;
	}

	/**
	 * Add the 12 bits information data and concatenate the bytes to it
	 * 
	 * @param inputBytes
	 *            the data byte sequence
	 * @return The input bytes with an header giving the type and size of the data
	 */
	public static int[] addInformations(int[] inputBytes) {
		int length = inputBytes.length;
		int outputLength = length + 2;
		int caseLength = outputLength-1;
		int[] outputBytes = new int[outputLength];
		for(int i = 0; i < outputLength; i++){
			switch(i){
				case 0 :
					outputBytes[i] = (64 & 240)|((length & 240)>>4);
					break;
				case 1 :
					outputBytes[i] = ((length & 15)<<4)|((inputBytes[i-1] & 240)>>4);
					break;
				default:{
					if(i == outputLength - 1){
						outputBytes[i] = ((inputBytes[i-2] & 15)<<4)|((0)>>4);
					}else{
						outputBytes[i] = ((inputBytes[i-2] & 15)<<4)|((inputBytes[i-1] & 240)>>4);
					}
				}
			}
		}
		return outputBytes;
	}

	/**
	 * Add padding bytes to the data until the size of the given array matches the
	 * finalLength
	 * 
	 * @param encodedData
	 *            the initial sequence of bytes
	 * @param finalLength
	 *            the minimum length of the returned array
	 * @return an array of length max(finalLength,encodedData.length) padded with
	 *         bytes 236,17
	 */
	public static int[] fillSequence(int[] encodedData, int finalLength) {
		int[] paddedSequence = new int[finalLength];
		if(encodedData.length >= finalLength){
			return encodedData;
		}else{

			for(int i = 0; i < encodedData.length; i++){
				paddedSequence[i] = encodedData[i];
			}

			for(int i = encodedData.length; i < finalLength; i++){
				if((paddedSequence[i-1] != 236)){
					paddedSequence[i] = 236;
				}else{
					paddedSequence[i] = 17;
				}
			}
			return paddedSequence;
		}
	}

	/**
	 * Add the error correction to the encodedData
	 * 
	 * @param encodedData
	 *            The byte array representing the data encoded
	 * @param eccLength
	 *            the version of the QR code
	 * @return the original data concatenated with the error correction
	 */
	public static int[] addErrorCorrection(int[] encodedData, int eccLength) {
		int[] correctionBytes = ErrorCorrectionEncoding.encode(encodedData, eccLength);
		int[] output = new int[encodedData.length + eccLength];
		for(int i = 0; i < encodedData.length; i++){
			output[i] = encodedData[i];
		}
		for(int i = encodedData.length; i-encodedData.length < eccLength; i++){
			output[i] = encodedData[i-eccLength];
		}
		return output;
	}

	/**
	 * Encode the byte array into a binary array represented with boolean using the
	 * most significant bit first.
	 * 
	 * @param data
	 *            an array of bytes
	 * @return a boolean array representing the data in binary
	 */
	public static boolean[] bytesToBinaryArray(int[] data) {
		boolean[] result = new boolean[data.length * BYTE_LENGTH];
		int index = 0;

		for (int _byte : data) {
			for (String bit : padLeft(Integer.toBinaryString(_byte), BYTE_LENGTH).split("")) {
				result[index] = bit.equals("1");
				index++;
			}
		}

		return result;
	}

}
