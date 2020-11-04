import java.util.Scanner;

public class RleProgram {

    //Prints the menu to the console
    public static void printMenu() {
        System.out.println("RLE Menu");
        System.out.println("--------");
        System.out.println("0. Exit");
        System.out.println("1. Load File");
        System.out.println("2. Load Test Image");
        System.out.println("3. Read RLE String");
        System.out.println("4. Read RLE Hex String");
        System.out.println("5. Read Data Hex String");
        System.out.println("6. Display Image");
        System.out.println("7. Display RLE String");
        System.out.println("8. Display Hex RLE Data");
        System.out.println("9. Display Hex Flat Data");
        System.out.println();
    }

    //Converts each index in the array to a hex character and then concatenates them together.
    //Uses no methods in order to convert from decimal to hex.
    public static String toHexString(byte[] data) {
        String temp = "";
        byte dataVal;
        for (int i = 0; i < data.length; i++) {
            dataVal = data[i];
            if (dataVal > 9) {
                temp += (char) ((dataVal - 10) + 'a');
            } else {
                temp += (char) ('0' + dataVal);
            }
        }
        return temp;
    }

    //Counts the number of indexes that have the same successive number.
    public static int countRuns(byte[] flatData) {
        int numRuns = 1; //accounts for the last stream which doesn't get counted
        int currentRuns = 0;
        for (int i = 0; i < flatData.length - 1; i++) {
            if (flatData[i] == flatData[i + 1]) {
                currentRuns++;
            } else {
                currentRuns = 1;
                numRuns++;
            }
            if (currentRuns > 15) {
                currentRuns = 0;
                numRuns++;
            }
        }
        return numRuns;
    }

    //Converts from the flat data to an rle byte array.
    //Uses boolean logic to determine if an rle pair should end and start the next one or continue.
    public static byte[] encodeRle(byte[] flatData) {
        byte[] array = new byte[countRuns(flatData) * 2];
        int j = 0;
        byte currentCount = 1;
        for (int i = 0; i < flatData.length - 1; i++) {
            if (flatData[i] == flatData[i + 1] && currentCount < 15) {
                currentCount++;
            } else if (currentCount > 15) {
                currentCount = 1;
                array[j] = 15;
                array[++j] = flatData[i];
                j++;
            } else {
                array[j] = currentCount;
                array[++j] = flatData[i];
                j++;
                currentCount = 1;
            }
        }
        //Since the for-loop doesn't work for the last index, this if-else statement fixes that.
        if (flatData[flatData.length - 1] == flatData[flatData.length - 2]) {
            array[array.length - 2] = currentCount;
            array[array.length - 1] = flatData[flatData.length - 1];
        } else {
            array[array.length - 2]++;
            array[array.length - 1] = flatData[flatData.length - 1];
        }
        return array;
    }

    //Determines the length of the decoded rle array.
    public static int getDecodedLength(byte[] rleData) {
        int decodedLength = 0;
        for (int i = 0; i < rleData.length; i += 2) {
            decodedLength += rleData[i];
        }
        return decodedLength;
    }

    //Converts the rle array into a flatened array.
    //Goes through each index to see how many of the next index should appear in the array.
    //Accomplishes this by using a while-loop inside of the for-loop.
    public static byte[] decodeRle(byte[] rleData) {
        byte[] array = new byte[getDecodedLength(rleData)];
        int j = 0;
        for (int i = 0; i < rleData.length; i++) {
            int currentIndex = rleData[i];
            byte currentVal = rleData[++i];
            while (currentIndex > 0) {
                array[j] = currentVal;
                j++;
                currentIndex--;
            }
        }
        return array;
    }

    //Converts the hex rle or raw string into a byte array of data.
    //If it's rle then the program knows this based on which command is issued in the beginning and
    //uses the output from this and runs it through decodeRle().
    public static byte[] stringToData(String dataString) {
        byte[] array = new byte[dataString.length()];
        for (int i = 0; i < array.length; i++) {
            if (Character.isAlphabetic(dataString.charAt(i))) {
                array[i] = (byte) (Character.toUpperCase(dataString.charAt(i)) - 'A' + 10);
            } else {
                array[i] = (byte) (dataString.charAt(i) - '0');
            }
        }
        return array;
    }

    //Converts the rle byte array into a rle string with a colon delimiter.
    public static String toRleString(byte[] rleData){
        String holding = "";
        byte dataVal;
        for(int i = 0; i < rleData.length; i++){
            dataVal = rleData[++i];
            holding += rleData[i-1];
            if (dataVal > 9) {
                holding += (char) ((dataVal - 10) + 'a');
            } else {
                holding += (char) ('0' + dataVal);
            }
            if(i != rleData.length-1){
                holding += ":";
            }
        }
        return holding;
    }

    //Converst the rle string with delimiters into a byte array. Data from this can then be
    //given to the decodeRle() method.
    public static byte[] stringToRle(String rleString){
        String[] holding = rleString.split(":");
        byte[] array = new byte[holding.length * 2];
        int i = 0;
        for(String temp : holding){
            array[i] = Byte.parseByte(temp.substring(0, temp.length()-1));
            array[++i] = Character.isAlphabetic(temp.charAt(temp.length()-1)) ? (byte)(Character.toUpperCase(temp.charAt(temp.length()-1)) - 'A' + 10):  (byte) (temp.charAt(temp.length()-1) - '0');
            i++;
        }
        return array;
    }

    public static void main(String[] args) {
        boolean running = true;
        Scanner scnr = new Scanner(System.in);
        int input;
        byte[] imageData = null;

        System.out.println("Welcome to the RLE image encoder!\n");
        System.out.println("Displaying Spectrum Image:");
        ConsoleGfx.displayImage(ConsoleGfx.testRainbow);
        System.out.println();

        //Allows the program to continuously run until the user selects 0 and the program stops.
        while (running) {
            printMenu();
            System.out.print("Select a Menu Option: ");
            input = scnr.nextInt();
            System.out.println();

            switch (input) {
                case 0:
                    running = false;
                    break;
                case 1:
                    System.out.print("Enter name of file to load: ");
                    String location = scnr.next();
                    imageData = ConsoleGfx.loadFile(location);
                    System.out.println();
                    break;
                case 2:
                    imageData = ConsoleGfx.testImage;
                    System.out.println("Test image data loaded.\n");
                    break;
                case 3:
                    System.out.print("Enter an RLE string to be decoded: ");
                    String in = scnr.next();
                    imageData = decodeRle(stringToRle(in));
                    System.out.println();
                    break;
                case 4:
                    System.out.print("Enter the hex string holding RLE data: ");
                    String data = scnr.next();
                    imageData = decodeRle(stringToData(data));
                    System.out.println();
                    break;
                case 5:
                    System.out.print("Enter the hex string holding flat data: ");
                    String flat = scnr.next();
                    imageData = stringToData(flat);
                    break;
                case 6:
                    System.out.println("Displaying image...");
                    ConsoleGfx.displayImage(imageData);
                    System.out.println();
                    break;
                case 7:
                    String rleRepresentation = toRleString(encodeRle(imageData));
                    System.out.println("RLE representation: " + rleRepresentation);
                    break;
                case 8:
                    String noDelim = toHexString(encodeRle(imageData));
                    System.out.println("RLE hex values: " + noDelim);
                    break;
                case 9:
                    System.out.println("Flat hex values: " + toHexString(imageData));
                    break;
                default:
                    break;
            }
        }
    }
}
