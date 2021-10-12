import java.io.FileReader;
import java.util.Scanner;

public class Loader {
    private static int location = 0;

    public static void load(String filename) throws Exception {
        Scanner scanner = new Scanner(new FileReader(filename));
        System.out.println("loading programs from " + filename);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.startsWith("// JOB")) {
                parseJob(line, scanner);
            } else {
                throw new Exception("expected job header, got '" + line + "'");
            }
        }
        System.out.println("read " + location + " words from " + filename);
        location = 0;
    }

    private static void parseJob(String jobLine, Scanner scanner) throws Exception {
        String[] jobHeader = jobLine.split(" ");
        if (jobHeader.length != 5)
            throw new Exception("job header incorrect '" + jobLine + "'");

        int diskLocation = location;
        int number, length, priority;
        try {
            number = Integer.parseInt(jobHeader[2], 16);
            length = Integer.parseInt(jobHeader[3], 16);
            priority = Integer.parseInt(jobHeader[4], 16);
        } catch (Exception e) {
            throw new Exception("job header incorrect '" + jobLine + "':\n" + e);
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.startsWith("0x")) {
                int instruction = (int) Long.parseLong(line.substring(2), 16);
                Device.disk[location++] = instruction;
            } else if (line.startsWith("// Data")) {
                String[] dataHeader = line.split(" ");
                if (dataHeader.length != 5)
                    throw new Exception("data header incorrect '" + line + "'");

                int inputLength, outputLength, tempLength;
                try {
                    inputLength = Integer.parseInt(dataHeader[2], 16);
                    outputLength = Integer.parseInt(dataHeader[3], 16);
                    tempLength = Integer.parseInt(dataHeader[4], 16);
                } catch (Exception e) {
                    throw new Exception("data header incorrect '" + line + "':\n" + e);
                }

                while (scanner.hasNext()) {
                    String dataLine = scanner.nextLine();
                    if (dataLine.startsWith("0x")) {
                        int data = (int) Long.parseLong(dataLine.substring(2), 16);
                        Device.disk[location++] = data;
                    } else if (dataLine.equals("// END")) {
                        break;
                    } else {
                        throw new Exception("unexpected line '" + line + "'");
                    }
                }

                Device.programs.add(new Program(number, length, priority, inputLength, outputLength, tempLength, diskLocation));

                break;
            } else {
                throw new Exception("expected data header, got '" + line + "'");
            }
        }
    }
}
