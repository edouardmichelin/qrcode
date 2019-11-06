package qrcode;

import java.awt.*;

public class MatrixConstruction {

	private static final int W = 0xFF_FF_FF_FF;
	private static final int BL = 0xFF_00_00_FF;
	private static final int R = 0xFF_FF_00_00;
	private static final int B = 0xFF_00_00_00;
	private static final int finderPatternSize = 7;
	private static final int alignmentPatternSize = 5;
	private static final int timingPatternPosition = 6;

	private static int matrixSize = 0;

	private static void addFinderPattern(int[][] matrix, int[] topLeftCornerIndex) {
		int offsetXBeg = topLeftCornerIndex[0], offsetXEnd = offsetXBeg + finderPatternSize;
		int offsetYBeg = topLeftCornerIndex[1], offsetYEnd = offsetYBeg + finderPatternSize;
		int offsetXAvg = (int) Math.ceil(offsetXBeg + offsetXEnd) / 2, offsetYAvg = (int) Math.ceil((offsetYBeg + offsetYEnd) / 2);

		for (int row = offsetXBeg - 1; row < offsetXEnd + 1; row++) {
			for (int col = offsetYBeg - 1; col < offsetYEnd + 1; col++) {
				if (col >= 0 && row >= 0 && col < matrixSize && row < matrixSize) {
					if ((col >= offsetYBeg && row >= offsetXBeg && col < offsetYEnd && row < offsetXEnd)) {
						if (
								(col == offsetYBeg || col == offsetYEnd - 1) ||
								(row == offsetXBeg || row == offsetXEnd - 1)
						) {
							matrix[row][col] = B;
						} else if (
								(col >  offsetYAvg - 2 && col < offsetYAvg + 2) &&
								(row > offsetXAvg - 2 && row < offsetXAvg + 2)
						) {
							matrix[row][col] = B;
						} else {
							matrix[row][col] = W;
						}
					} else {
						matrix[row][col] = W;
					}
				}
			}
		}
	}

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
		int[][] matrix = initializeMatrix(version);

		addFinderPatterns(matrix);
		addTimingPatterns(matrix);
		addAlignmentPatterns(matrix, version);
		addDarkModule(matrix);
		addFormatInformation(matrix, mask);

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
		int offset = matrixSize - finderPatternSize;
		addFinderPattern(matrix, new int[] {0, 0});
		addFinderPattern(matrix, new int[] {offset, 0});
		addFinderPattern(matrix, new int[] {0, offset});
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
		if (version < 2) return;
		int offsetEnd = matrixSize - 4, offsetBeg = offsetEnd - alignmentPatternSize;
		int midPos = (offsetBeg + offsetEnd) / 2;

		for (int row = offsetBeg; row < offsetEnd; row++) {
			for (int col = offsetBeg; col < offsetEnd; col++) {
				if ((col == offsetBeg || col == offsetEnd - 1) || (row == offsetBeg || row == offsetEnd - 1)) {
					matrix[col][row] = B;
					matrix[row][col] = B;
				} else if (row == midPos && row == col) {
					matrix[row][row] = B;
				} else {
					matrix[row][col] = W;
				}
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
		int bit = 0;
		int offset = finderPatternSize + 1;

		for (int index = 0; index < matrixSize; index++) {
			if (index > offset - 1 && index < matrixSize - offset) {
				int color = bit == 0 ? B : W;
				matrix[index][timingPatternPosition] = color;
				matrix[timingPatternPosition][index] = color;
				bit = (bit + 1) % 2;
			}
		}
	}

	/**
	 * Add the dark module to the matrix
	 *
	 * @param matrix
	 *            the 2-dimensional array representing the QR code
	 */
	public static void addDarkModule(int[][] matrix) {
		int offset = finderPatternSize + 1;
		matrix[offset][matrixSize - offset] = B;
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
		boolean[] maskInfo = QRCodeInfos.getFormatSequence(mask);

		int index = 0;
		for (int col = matrixSize - 1; col >= 0; col--) {
			if (
					col != timingPatternPosition &&
					(col > (matrixSize - finderPatternSize - 1) || col < (finderPatternSize + 2))
			) {
				matrix[finderPatternSize + 1][col] = maskInfo[index] ? B : W;
				index++;
			}
		}

		index = 0;
		for (int row = 0; row < matrixSize; row++) {
			if (
					row != timingPatternPosition &&
					(row > (matrixSize - finderPatternSize - 2) || row < (finderPatternSize + 1))
			) {
				matrix[row][finderPatternSize + 1] = maskInfo[index] ? B : W;
				index++;
			}
		}
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
		boolean isMasked;
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
			case 6: isMasked = ((col * row) % 2 + (col * row) % 3) % 2 == 0;
				break;
			case 7: isMasked = ((col + row) % 2 + (col * row) % 3) % 2 == 0;
				break;
			default: return dataBit ? B : W;
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

		int index = 0;
		boolean turn = true;
		boolean bit;
		boolean nextBit;

		for (int col = matrixSize - 1; col >= 0; col -= 2) {
			if(col == timingPatternPosition) col -= 1;

			int turnIndex = matrixSize -1;
			turn = !turn;

			for (int row = matrixSize - 1; row >= 0; row--) {
				int fillRow = turn ? row - turnIndex : row;

				if(index >= data.length || data.length == 0){
					bit = false;
					nextBit = false;
					index = 0;
				}else{
					bit = data.length > 0 ? data[index] : false;
					nextBit = (data.length > 0 && index <= data.length - 2) ? data[index + 1] : false;
				}

				if (matrix[col][fillRow] != 0 ){
					if(matrix[col-1][fillRow] != 0 ){
						if(turn) turnIndex -= 2;
						continue;
					}else{
						matrix[col - 1][fillRow] = maskColor(col-1, fillRow, bit, mask);
						index += 1;
						if(turn) turnIndex -= 2;
						continue;
					}
				}

				matrix[col][fillRow] = maskColor(col, fillRow, bit, mask);

				if(index == data.length-1){
					continue;
				}else {
					matrix[col - 1][fillRow] = maskColor(col - 1, fillRow, nextBit, mask);
				}
				index += 2;

				if(turn) turnIndex -= 2;
			}
		}
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
