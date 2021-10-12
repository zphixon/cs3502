import java.util.ArrayList;

public class Device {
    public static void main(String[] args) throws Exception {
        // sets up Device.disk with the programs
        Loader.load("programs.txt");

        // manually move the program to ram to execute
        Program program1 = programs.get(3);
        System.out.println(program1);
        for (int i = program1.diskLocation(); i < program1.diskLocation() + program1.totalLength(); i++)
            ram[i - program1.diskLocation()] = disk[i];

        // run a single program
        CPU cpu = new CPU();
        while (cpu.step())
            ;

        // show the output of the program
        dumpMemory(program1.outputStart(), program1.outputStart() + program1.outputLength());
    }

    public static final int RAM_WORDS = 1024;
    public static final int DISK_WORDS = 2048;

    public static int[] ram = new int[RAM_WORDS];
    public static int[] disk = new int[DISK_WORDS];

    public static ArrayList<Program> programs = new ArrayList<>();
    public static ArrayList<Process> ready = new ArrayList<>();
    public static ArrayList<Process> waiting = new ArrayList<>();
    public static ArrayList<Program> finished = new ArrayList<>();

    public static void dumpDisk() {
        dumpDisk(0, DISK_WORDS);
    }

    public static void dumpDisk(int start, int upTo) {
        for (int i = start; i < upTo; i++) {
            System.out.printf("    %08x", disk[i]);

            if (i % 16 == 15)
                System.out.println();
        }
    }

    public static void dumpMemory() {
        dumpMemory(0, RAM_WORDS);
    }

    public static void dumpMemory(int start, int upTo) {
        for (int i = start; i < upTo; i++) {
            System.out.printf("    %08x", ram[i]);

            if (i % 16 == 15)
                System.out.println();
        }
    }
}
