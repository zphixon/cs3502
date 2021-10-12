import java.io.FileReader;
import java.util.Scanner;

public class Loader {
    private static int location = 0;

    public static void load(String filename) throws Exception {
        // read the file line by line
        Scanner scanner = new Scanner(new FileReader(filename));
        System.out.println("loading programs from " + filename);

        // until we run out of lines to read
        while (scanner.hasNext()) {
            // read a line
            String line = scanner.nextLine();
            if (line.startsWith("// JOB")) {
                parseJob(line, scanner);
            } else {
                throw new Exception("expected job header, got '" + line + "'");
            }
        }

        System.out.println("read " + location + " words from " + filename);

        // we never load more than once but this just makes me feel better
        location = 0;
    }

    private static void parseJob(String jobLine, Scanner scanner) throws Exception {
        // job header format is '// JOB number length priority'
        String[] jobHeader = jobLine.split(" ");
        if (jobHeader.length != 5)
            throw new Exception("job header incorrect '" + jobLine + "'");

        // this is where the program is located on disk
        int diskLocation = location;

        // pull out job info from the job header
        int number, length, priority;
        try {
            number = Integer.parseInt(jobHeader[2], 16);
            length = Integer.parseInt(jobHeader[3], 16);
            priority = Integer.parseInt(jobHeader[4], 16);
        } catch (Exception e) {
            throw new Exception("job header incorrect '" + jobLine + "':\n" + e);
        }

        // read the program code
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.startsWith("0x")) {
                // use Long.parseLong because java doesn't have unsigned integers
                // not a problem, since casting to an int here is just a bitwise AND of the low 32 bits
                int instruction = (int) Long.parseLong(line.substring(2), 16);
                Device.disk[location++] = instruction;
            } else if (line.startsWith("// Data")) {
                // data header format is '// Data input output temp'
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

                // read the data
                // we could probably skip over the output/temp parts, but I haven't verified that it's all 0s
                while (scanner.hasNext()) {
                    String dataLine = scanner.nextLine();
                    if (dataLine.startsWith("0x")) {
                        // again, there's no funky sign bit stuff happening it's just a bitwise and
                        int data = (int) Long.parseLong(dataLine.substring(2), 16);
                        Device.disk[location++] = data;
                    } else if (dataLine.equals("// END")) {
                        break;
                    } else {
                        throw new Exception("unexpected line '" + line + "'");
                    }
                }

                // add the program to the list of programs
                Device.programs.add(new Program(number, length, priority, inputLength, outputLength, tempLength, diskLocation));

                break;
            } else {
                throw new Exception("expected data header, got '" + line + "'");
            }
        }
    }
}
