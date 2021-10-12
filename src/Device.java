import java.util.ArrayList;

public class Device {
    public static void main(String[] args) throws Exception {
        // sets up Device.disk with the programs
        Loader.load("programs.txt");
        CPU cpu = new CPU();

        // manually move the program to ram to execute
        for (int i = 0; i < programs.size(); i++) {
            Program program = programs.get(i);
            System.out.println(program);
            for (int j = program.diskLocation(); j < program.diskLocation() + program.totalLength(); j++)
                ram[j - program.diskLocation()] = disk[j];
            Process proc = new Process(program, 0, 0);
            cpu.switchContext(proc);

            // run a single program
            while (cpu.step())
                ;

            // show the output of the program
            dumpMemory(program.outputStart(), program.outputStart() + program.outputLength());
            System.out.println(proc);
            System.out.println();
        }
    }

    public static final int RAM_WORDS = 1024;
    public static final int DISK_WORDS = 2048;

    public static int[] ram = new int[RAM_WORDS];
    public static int[] disk = new int[DISK_WORDS];

    public static ArrayList<Program> programs = new ArrayList<>();
    public static ArrayList<Process> ready = new ArrayList<>();
    public static ArrayList<Process> waiting = new ArrayList<>();
    public static ArrayList<Process> finished = new ArrayList<>();

    public static void dumpDisk() {
        dumpDisk(0, DISK_WORDS);
    }

    public static void dumpDisk(int start, int upTo) {
        for (int i = start; i < upTo; i++) {
            System.out.printf("    %08x", disk[i]);

            if (i % 16 == 15)
                System.out.println();
        }
        System.out.println();
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
        System.out.println();
    }
}
