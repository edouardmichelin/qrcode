package qrcode;

public class Main {

	public static final String INPUT = "Bonne journée";
	/*
	 * Parameters
	 */
	public static final int VERSION = 4;
	public static final int MASK = 0;
	public static final int SCALING = 20;

	public static void main(String[] args) {
		/*
		 * Encoding
		 */
		boolean[] encodedData = DataEncoding.byteModeEncoding(INPUT, VERSION);

		for (boolean b : encodedData) {
			System.out.print(b ? "1" : "0");
		}
		
		/*
		 * image
		 */
		// int[][] qrCode = MatrixConstruction.renderQRCodeMatrix(VERSION, encodedData,MASK);

		/*
		 * Visualization
		 */
		// Helpers.show(qrCode, SCALING);
	}

}
