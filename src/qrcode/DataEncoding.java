package qrcode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import reedsolomon.ErrorCorrectionEncoding;

public final class DataEncoding {

	private static final Charset CHARSET = StandardCharsets.ISO_8859_1;
	private static final int BYTE_LENGTH = 8;

	/**
	 *
	 * @param input
	 * 			The string to pad to the left
	 * @param length
	 * 			The final length of the padded string
	 * @return
	 * 			The left padded string with a size of the given length
	 */
	private static String padLeft(String input, int length) {
		if (length == input.length()) return input;

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
		int maxInputLength = QRCodeInfos.getMaxInputLength((version));
		int maxCodeWordsLength = QRCodeInfos.getCodeWordsLength(version);
		int maxECCLength = QRCodeInfos.getECCLength(version);
		int[] encodedString;

		encodedString = encodeString(input, maxInputLength);
		encodedString = addInformations(encodedString);
		encodedString = fillSequence(encodedString, maxCodeWordsLength);
		encodedString = addErrorCorrection(encodedString, maxECCLength);

		return bytesToBinaryArray(encodedString);
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
		int fixedInputLength = Math.min(encodedString.length, maxLength);
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
		int offset = 2;
		int length = inputBytes.length;

		if (length < (offset + 1)) return new int[] {64, 0};

		int outputLength = length + offset;
		int[] outputBytes = new int[outputLength];

		outputBytes[0] = (64 & 240) | ((length & 240) >> 4);
		outputBytes[1] = ((length & 15) << 4) | ((inputBytes[0] & 240) >> 4);

		for(int i = offset; i < outputLength - 1; i++){
			outputBytes[i] = ((inputBytes[i - offset] & 15) << 4) | ((inputBytes[i - 1] & 240) >> 4);
		}

		outputBytes[outputLength - 1] = ((inputBytes[outputLength - 3] & 15) << 4);

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
		int length = encodedData.length;
		int index = 0;

		System.arraycopy(encodedData, 0, paddedSequence, 0, length);

		if (length >= finalLength) return paddedSequence;

		for (int i = length; i < finalLength; i++) {
			paddedSequence[i] = index % 2 == 0 ? 236 : 17;
			index++;
		}

		return paddedSequence;
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
		int inputLength = encodedData.length;
		int[] correctionBytes = ErrorCorrectionEncoding.encode(encodedData, eccLength);
		int[] output = new int[inputLength + eccLength];

		System.arraycopy(encodedData, 0, output, 0, inputLength);
		System.arraycopy(correctionBytes, 0, output, inputLength, eccLength);

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

		for (int dataByte : data) {
			for (String bit : padLeft(Integer.toBinaryString(dataByte), BYTE_LENGTH).split("")) {
				result[index] = bit.equals("1");
				index++;
			}
		}

		return result;
	}

}
