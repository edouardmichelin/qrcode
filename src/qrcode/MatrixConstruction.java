package qrcode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MatrixConstruction {

	/*
	 * Constants defining the color in ARGB format
	 * 
	 * W = White integer for ARGB
	 * 
	 * B = Black integer for ARGB
	 * 
	 * both needs to have their alpha component to 255
	 */
	// TODO add constant for White pixel
	// TODO add constant for Black pixel

	private static final int W = 0xFF_FF_FF_FF; // new Color(255, 255, 255, 255).getRGB();
	private static final int B = 0xFF_00_00_00; // new Color(255, 0, 0, 0).getRGB();

	private static int matrixSize = 0;
	private static int finderPatternSize = 8;

	// private final int[] maximumLengthForVersion = new int[] {17, 32, 53, 78};
	

	// ...  MYDEBUGCOLOR = ...;
	// feel free to add your own colors for debugging purposes

	/**
	 * Create the matrix of a QR code with the given data.
	 * 
	 * @param version
	 *            The version of the QR code
	 * @param data
	 *            The data to be written on the QR code
	 * @param mask
	 *            The mask used on the data. If not valid (e.g: -1), then no mask is
	 *            used.
	 * @return The matrix of the QR code
	 */
	public static int[][] renderQRCodeMatrix(int version, boolean[] data, int mask) {

		/*
		 * PART 2
		 */
		int[][] matrix = constructMatrix(version, mask);
		/*
		 * PART 3
		 */
		addDataInformation(matrix, data, mask);

		return matrix;
	}

	/*
	 * =======================================================================
	 * 
	 * ****************************** PART 2 *********************************
	 * 
	 * =======================================================================
	 */

	/**
	 * Create a matrix (2D array) ready to accept data for a given version and mask
	 * 
	 * @param version
	 *            the version number of QR code (has to be between 1 and 4 included)
	 * @param mask
	 *            the mask id to use to mask the data modules. Has to be between 0
	 *            and 7 included to have a valid matrix. If the mask id is not
	 *            valid, the modules would not be not masked later on, hence the
	 *            QRcode would not be valid
	 * @return the qrcode with the patterns and format information modules
	 *         initialized. The modules where the data should be remain empty.
	 */
	public static int[][] constructMatrix(int version, int mask) {
		// TODO Implementer
		int[][] matrix = initializeMatrix(version);

		addFinderPatterns(matrix);
		addAlignmentPatterns(matrix, version);

		return matrix;

	}

	/**
	 * Create an empty 2d array of integers of the size needed for a QR code of the
	 * given version
	 * 
	 * @param version
	 *            the version number of the qr code (has to be between 1 and 4
	 *            included
	 * @return an empty matrix
	 */
	public static int[][] initializeMatrix(int version) {
		matrixSize = QRCodeInfos.getMatrixSize(version);
		return new int[matrixSize][matrixSize];
	}

	/**
	 * Add all finder patterns to the given matrix with a border of White modules.
	 * 
	 * @param matrix
	 *            the 2D array to modify: where to add the patterns
	 */
	public static void addFinderPatterns(int[][] matrix) {
		for (int row = 0; row < matrixSize; row++) {
			for (int col = 0; col < matrixSize; col++) {
				if (row < finderPatternSize || row > matrixSize - (finderPatternSize + 1)) {
					if (
							(col < finderPatternSize || col  > matrixSize - (finderPatternSize + 1)) &&
							(row < finderPatternSize || col < finderPatternSize)
					) {
						if ((row == (finderPatternSize - 1) || col == (finderPatternSize - 1)) ||
								((row == (matrixSize - (finderPatternSize))) ||
										(col == (matrixSize - (finderPatternSize))))
						) {
							matrix[col][row] = W;
						} else {
							matrix[col][row] = B;
							if (
									(row > 0 &&
									row < finderPatternSize - 2 ) ||
									(row < matrixSize - 1 &&
									row > matrixSize - finderPatternSize + 1)
							) {
								if (
									(col > 0 && col < finderPatternSize - 2) ||
									(col > matrixSize - finderPatternSize + 1) && col < matrixSize - 1) {
									matrix[col][row] = W;
									if ((col > 1 && col < finderPatternSize - 3) ||
											(col > matrixSize - finderPatternSize + 2 && col < matrixSize - 2)) {
										if ((row > 1 && row < finderPatternSize - 3) ||
												(row > matrixSize - finderPatternSize + 2 && row < matrixSize - 2)) {
											matrix[col][row] = B;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Add the alignment pattern if needed, does nothing for version 1
	 * 
	 * @param matrix
	 *            The 2D array to modify
	 * @param version
	 *            the version number of the QR code needs to be between 1 and 4
	 *            included
	 */
	public static void addAlignmentPatterns(int[][] matrix, int version) {
		int bit = 0;
		int alignmentPatternPosition = 6;
		for (int index = 0; index < matrixSize; index++) {
			if (index > finderPatternSize - 1 && index < matrixSize - finderPatternSize) {
				matrix[index][alignmentPatternPosition] = bit == 0 ? B : W;
				matrix[alignmentPatternPosition][index] = bit == 0 ? B : W;
				bit = (bit + 1) % 2;
			}
		}
	}

	/**
	 * Add the timings patterns
	 * 
	 * @param matrix
	 *            The 2D array to modify
	 */
	public static void addTimingPatterns(int[][] matrix) {
		// TODO Implementer
	}

	/**
	 * Add the dark module to the matrix
	 * 
	 * @param matrix
	 *            the 2-dimensional array representing the QR code
	 */
	public static void addDarkModule(int[][] matrix) {
		// TODO Implementer
	}

	/**
	 * Add the format information to the matrix
	 * 
	 * @param matrix
	 *            the 2-dimensional array representing the QR code to modify
	 * @param mask
	 *            the mask id
	 */
	public static void addFormatInformation(int[][] matrix, int mask) {
		// TODO Implementer
	}

	/*
	 * =======================================================================
	 * ****************************** PART 3 *********************************
	 * =======================================================================
	 */

	/**
	 * Choose the color to use with the given coordinate using the masking 0
	 * 
	 * @param col
	 *            x-coordinate
	 * @param row
	 *            y-coordinate
	 * @param color
	 *            : initial color without masking
	 * @return the color with the masking
	 */
	public static int maskColor(int col, int row, boolean dataBit, int masking) {
		boolean isMasked = false;
		switch (masking) {
			case 0: isMasked = (col + row) % 2 == 0;
				break;
			case 1: isMasked = row % 2 == 0;
				break;
			case 2: isMasked = col % 3 == 0;
				break;
			case 3: isMasked = (col + row) % 3 == 0;
				break;
			case 4: isMasked = (Math.floor(col / 3) + Math.floor(row / 2)) % 2 == 0;
				break;
			case 5: isMasked = ((col * row) % 2 + (col * row) % 3) == 0;
				break;
			case 6:
			case 7: isMasked = ((col * row) % 2 + (col * row) % 3) % 2 == 0;
				break;
		}

		return dataBit && !isMasked || !dataBit && isMasked ? B : W;
	}

	/**
	 * Add the data bits into the QR code matrix
	 * 
	 * @param matrix
	 *            a 2-dimensionnal array where the bits needs to be added
	 * @param data
	 *            the data to add
	 */
	public static void addDataInformation(int[][] matrix, boolean[] data, int mask) {
		// TODO Implementer

	}

	/*
	 * =======================================================================
	 * 
	 * ****************************** BONUS **********************************
	 * 
	 * =======================================================================
	 */

	/**
	 * Create the matrix of a QR code with the given data.
	 * 
	 * The mask is computed automatically so that it provides the least penalty
	 * 
	 * @param version
	 *            The version of the QR code
	 * @param data
	 *            The data to be written on the QR code
	 * @return The matrix of the QR code
	 */
	public static int[][] renderQRCodeMatrix(int version, boolean[] data) {

		int mask = findBestMasking(version, data);

		return renderQRCodeMatrix(version, data, mask);
	}

	/**
	 * Find the best mask to apply to a QRcode so that the penalty score is
	 * minimized. Compute the penalty score with evaluate
	 * 
	 * @param data
	 * @return the mask number that minimize the penalty
	 */
	public static int findBestMasking(int version, boolean[] data) {
		// TODO BONUS
		return 0;
	}

	/**
	 * Compute the penalty score of a matrix
	 * 
	 * @param matrix:
	 *            the QR code in matrix form
	 * @return the penalty score obtained by the QR code, lower the better
	 */
	public static int evaluate(int[][] matrix) {
		//TODO BONUS
	
		return 0;
	}

}
