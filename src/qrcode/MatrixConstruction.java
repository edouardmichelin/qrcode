package qrcode;
import java.lang.Math;

import java.lang.Math;

public class MatrixConstruction {

	private static final int W = 0xFF_FF_FF_FF;
	private static final int B = 0xFF_00_00_00;
	private static final int FINDER_PATTERN_SIZE = 7;
	private static final int ALIGNMENT_PATTERN_SIZE = 5;
	private static final int TIMING_PATTERN_POSITION = 6;

	private static int matrixSize = 0;

    /**
     *
     * @param matrix
     *          The 2D array to modify: where to add the pattern
     * @param topLeftCornerPosX
     *          The X coordinate of the finder pattern's top left corner
     * @param topLeftCornerPosY
     *          The Y coordinate of the finder pattern's top left corner
     */
	private static void addFinderPattern(int[][] matrix, int topLeftCornerPosX, int topLeftCornerPosY) {
		int offsetXBeg = topLeftCornerPosX, offsetXEnd = offsetXBeg + FINDER_PATTERN_SIZE;
		int offsetYBeg = topLeftCornerPosY, offsetYEnd = offsetYBeg + FINDER_PATTERN_SIZE;
		int offsetXAvg = (int) Math.ceil(offsetXBeg + offsetXEnd) / 2, offsetYAvg = (int) Math.ceil((offsetYBeg + offsetYEnd) / 2);

		// -1 and +1 so we can draw the white separator around the finder pattern
		for (int col = offsetXBeg - 1; col < offsetXEnd + 1; col++) {
			for (int row = offsetYBeg - 1; row < offsetYEnd + 1; row++) {
				if (row >= 0 && col >= 0 && row < matrixSize && col < matrixSize) {
				    // check the col and row values so that we don't go out of bounds.
					if ((row >= offsetYBeg && col >= offsetXBeg && row < offsetYEnd && col < offsetXEnd)) {
						if (
								(row == offsetYBeg || row == offsetYEnd - 1) ||
								(col == offsetXBeg || col == offsetXEnd - 1)
						) {
						    // draws the black square that contains the finder pattern
							matrix[col][row] = B;
						} else if (
								(row >  offsetYAvg - 2 && row < offsetYAvg + 2) &&
								(col > offsetXAvg - 2 && col < offsetXAvg + 2)
						) {
                            // draws the 3x3 black square at the center
							matrix[col][row] = B;
						} else {
							matrix[col][row] = W;
						}
					} else {
					    // draws the finder pattern's separator
						matrix[col][row] = W;
					}
				}
			}
		}
	}

    /**
     *
     * @param matrix
     *          The 2D array to modify: where to place the alignment pattern
     */
	public static void addAlignmentPattern(int[][] matrix, int topLeftCornerPosX, int topLeftCornerPosY) {
        int offsetXBeg = topLeftCornerPosX, offsetXEnd = offsetXBeg + ALIGNMENT_PATTERN_SIZE;
        int offsetYBeg = topLeftCornerPosY, offsetYEnd = offsetYBeg + ALIGNMENT_PATTERN_SIZE;
        int midPosX = (offsetXBeg + offsetXEnd) / 2, midPosY = (offsetYBeg + offsetYEnd) / 2;

        for (int col = offsetXBeg; col < offsetXEnd; col++) {
            for (int row = offsetYBeg; row < offsetYEnd; row++) {
                if ((row == offsetYBeg || row == offsetYEnd - 1) || (col == offsetXBeg || col == offsetXEnd - 1)) {
                    // draws the black square that contains the alignment pattern
                    matrix[col][row] = B;
                } else if (col == midPosX && row == midPosY) {
                    // draws the black module at the center
                    matrix[col][row] = B;
                } else {
                    matrix[col][row] = W;
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
        // addDataInformation(matrix, data, mask);

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
        addAlignmentPatterns(matrix, version);
		addTimingPatterns(matrix);
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
	    // offset gets the index of the finder pattern's "starting point"
		int offset = matrixSize - FINDER_PATTERN_SIZE;
		addFinderPattern(matrix, 0, 0);
		addFinderPattern(matrix, offset, 0);
		addFinderPattern(matrix, 0, offset);
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
		int posOne = 0, posTwo = 0;

        int[] alignmentPatternsPositions = Extensions.getAlignmentPatternsPositions(version);
        for (int i = 0; i < alignmentPatternsPositions.length; i++) {
            int patternPosition = alignmentPatternsPositions[i];
            for (int j = 0; j < alignmentPatternsPositions.length; j++) {
                posOne = alignmentPatternsPositions[j];
                posTwo = patternPosition;
                if (matrix[posOne][posTwo] == 0 && matrix[posTwo][posOne] == 0) {
                    addAlignmentPattern(matrix, posOne - 2, posTwo - 2);
                    addAlignmentPattern(matrix, posTwo - 2, posOne - 2);
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
		// we need an offset because of the finder pattern's separator of width 1
		int offset = FINDER_PATTERN_SIZE + 1;

		for (int index = 0; index < matrixSize; index++) {
			if (index > offset - 1 && index < matrixSize - offset) {
				matrix[index][TIMING_PATTERN_POSITION] = bit == 0 ? B : W;
				matrix[TIMING_PATTERN_POSITION][index] = bit == 0 ? B : W;
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
        // we need an offset because of the finder pattern's separator of width 1
		int offset = FINDER_PATTERN_SIZE + 1;
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
		for (int row = matrixSize - 1; row >= 0; row--) {
			if (
					row != TIMING_PATTERN_POSITION &&
					(row > (matrixSize - FINDER_PATTERN_SIZE - 1) || row < (FINDER_PATTERN_SIZE + 2))
			) {
				matrix[FINDER_PATTERN_SIZE + 1][row] = maskInfo[index] ? B : W;
				index++;
			}
		}

		index = 0;
		for (int col = 0; col < matrixSize; col++) {
			if (
					col != TIMING_PATTERN_POSITION &&
					(col > (matrixSize - FINDER_PATTERN_SIZE - 2) || col < (FINDER_PATTERN_SIZE + 1))
			) {
				matrix[col][FINDER_PATTERN_SIZE + 1] = maskInfo[index] ? B : W;
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
        boolean desc = true;
        boolean bit, nextBit;
		int index = 0;
		int turnIndex, rowIndex;
		int dataLength = data.length;

		for (int col = matrixSize - 1; col >= 0; col -= 2) {
			if (col == TIMING_PATTERN_POSITION) col -= 1;

			turnIndex = matrixSize -1;
			desc = !desc;

			for (int row = matrixSize - 1; row >= 0; row--) {
				rowIndex = desc ? row - turnIndex : row;

				if (index >= dataLength || dataLength == 0) {
					bit = false;
					nextBit = false;
					index = 0;
				} else {
					bit = dataLength > 0 && data[index];
					nextBit = (dataLength > 0 && index <= dataLength - 2) && data[index + 1];
				}

                if (matrix[col][rowIndex] != 0) {
                    if (matrix[col - 1][rowIndex] == 0) {
                        matrix[col - 1][rowIndex] = maskColor(col - 1, rowIndex, bit, mask);
                        index += 1;
                    }
                    if (desc) turnIndex -= 2;
                    continue;
                }

				matrix[col][rowIndex] = maskColor(col, rowIndex, bit, mask);

				if (index == dataLength - 1) continue;

				matrix[col - 1][rowIndex] = maskColor(col - 1, rowIndex, nextBit, mask);
				index += 2;

				if (desc) turnIndex -= 2;
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
        int[] maskIds = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
        int firstMaskId = maskIds[0];

        int bestMaskId = firstMaskId;
        int lowestPenalty = evaluate(renderQRCodeMatrix(version, data, firstMaskId));

        for (int maskId : maskIds) {
            if (maskId == firstMaskId) continue;
            int penalty = evaluate(renderQRCodeMatrix(version, data, maskId));
            if (penalty < lowestPenalty) {
                lowestPenalty = penalty;
                bestMaskId = maskId;
            }
        }

		return bestMaskId;
	}

	/**
	 * Compute the penalty score of a matrix
	 *
	 * @param matrix:
	 *            the QR code in matrix form
	 * @return the penalty score obtained by the QR code, lower the better
	 */
	public static int evaluate(int[][] matrix) {
        int prevMultiple, nextMultiple;
        int blackRatio;
        int penalty = 0;
        int countForCol = 0, countForRow = 0;
        int lastColModule = B, lastRowModule = B;
        double blackModules = 0d;
        boolean isWhiteSquare, isBlackSquare;
        int[] penaltySequence1 = {W, W, W, W, B, W, B, B, B, W, B};
        int[] penaltySequence2 = {B, W, B, B, B, W, B, W, W, W , W};
        boolean matchPenSeq1, matchPenSeq2;

        for (int col = 0; col < matrixSize; col++) {
            for (int row = 0; row < matrixSize; row++) {
                if (matrix[col][row] == B)
                    blackModules += 1;

                // Checks rows 5 reps
                if (matrix[col][row] == lastRowModule) {
                    countForRow += 1;
                } else {
                    lastRowModule = matrix[col][row];
                    countForRow = 1;
                }
                // Checks columns 5 reps (inverted col row)
                if (matrix[row][col] == lastColModule)
                    countForCol += 1;
                else {
                    lastColModule = matrix[row][col];
                    countForCol = 1;
                }

                if (countForRow == 5)
                    penalty += 3;
                else if (countForRow > 5)
                    penalty += 1;

                if (countForCol == 5)
                    penalty += 3;
                else if (countForCol > 5)
                    penalty += 1;

                if(row == matrixSize - 1){
                    countForCol = 0;
                    countForRow = 0;
                }

                // check 2x2 reps
                if (col > 0 && row > 0) {
                    isWhiteSquare =
                            matrix[col][row] == W &&
                                    matrix[col - 1][row] == W &&
                                    matrix[col][row - 1] == W &&
                                    matrix[col - 1][row - 1] == W;

                    isBlackSquare =
                            matrix[col][row] == B &&
                                    matrix[col - 1][row] == B &&
                                    matrix[col][row - 1] == B &&
                                    matrix[col - 1][row - 1] == B;

                    if (isWhiteSquare || isBlackSquare)
                        penalty += 3;
                }

                // check sequences
                matchPenSeq1 = true;
                matchPenSeq2 = true;
                if (row < matrixSize - 10) {
                    for (int i = 0; i < 11; i++) {
                        int moduleRow = matrix[col][row + i];
                        int moduleCol = matrix[row + i][col];
                        if (penaltySequence1[i] != moduleRow)
                            matchPenSeq1 = false;
                        if (penaltySequence2[i] != moduleCol)
                            matchPenSeq2 = false;
                    }

                    if (matchPenSeq1) penalty += 40;
                    if (matchPenSeq2) penalty += 40;
                }
            }
        }

        // last penalty formula
        blackRatio = (int) Math.round((blackModules / (matrixSize * matrixSize)) * 100);
        prevMultiple = Math.abs((blackRatio - (blackRatio % 5)) - 50);
        nextMultiple = Math.abs(((blackRatio + 5) - (blackRatio % 5)) - 50);

        penalty += prevMultiple <= nextMultiple ? prevMultiple * 2 : nextMultiple * 2;

        return penalty;
	}

}
