package qrcode;

public class Extensions {

    public static int[] getAlignmentPatternsPositions(int version) {
        switch (version) {
            case 2: return new int[] {6, 18};
            case 3: return new int[] {6, 22};
            case 4: return new int[] {6, 26};
            case 5: return new int[] {6, 30};
            case 6: return new int[] {6, 34};
            case 7: return new int[] {6, 22, 38};
            case 8: return new int[] {6, 24, 42};
            case 9: return new int[] {6, 26, 46};
            case 10: return new int[] {6, 28, 50};
            case 11: return new int[] {6, 30, 54};
            case 12: return new int[] {6, 32, 58};
            case 13: return new int[] {6, 34, 62};
            case 14: return new int[] {6, 26, 46, 66};
            case 15: return new int[] {6, 26, 48, 70};
            case 16: return new int[] {6, 26, 50, 74};
            case 17: return new int[] {6, 30, 54, 78};
            case 18: return new int[] {6, 30, 56, 82};
            case 19: return new int[] {6, 30, 58, 86};
            case 20: return new int[] {6, 34, 62, 90};
            case 21: return new int[] {6, 28, 50, 72, 94};
            case 22: return new int[] {6, 26, 50, 74, 98};
            case 23: return new int[] {6, 30, 54, 78, 102};
            case 24: return new int[] {6, 28, 54, 80, 106};
            case 25: return new int[] {6, 32, 58, 84, 110};
            case 26: return new int[] {6, 30, 58, 86, 114};
            case 27: return new int[] {6, 34, 63, 90, 118};
            case 28: return new int[] {6, 26, 50, 74, 98, 122};
            case 29: return new int[] {6, 30, 54, 78, 102, 126};
            case 30: return new int[] {6, 26, 52, 78, 104, 130};
            case 31: return new int[] {6, 30, 56, 82, 108, 134};
            case 32: return new int[] {6, 34, 60, 86, 112, 138};
            case 33: return new int[] {6, 30, 58, 86, 114, 142};
            case 34: return new int[] {6, 34, 62, 90, 118, 146};
            case 35: return new int[] {6, 30, 54, 78, 102, 126, 150};
            case 36: return new int[] {6, 24, 50, 76, 102, 128, 154};
            case 37: return new int[] {6, 28, 54, 80, 106, 132, 158};
            case 38: return new int[] {6, 32, 58, 84, 110, 136, 162};
            case 39: return new int[] {6, 26, 54, 82, 110, 138, 166};
            case 40: return new int[] {6, 30, 58, 86, 114, 142, 170};
            default: return new int[0];
        }
    }
}
