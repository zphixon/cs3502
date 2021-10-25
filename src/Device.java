import java.util.ArrayList;

public class Device {
    public static void main(String[] args) throws Exception {
        // sets up Device.disk with the programs
        Loader.load("programs.txt");

        tickTock();
    }

    private static void sequential() throws Exception {
        CPU cpu = new CPU();

        // manually move the program to ram to execute
        int ramLocation = 0;
        for (Program program : programs) {
            System.out.println(program);
            for (int j = program.diskLocation(); j < program.diskLocation() + program.totalLength(); j++)
                ram[ramLocation + j - program.diskLocation()] = disk[j];
            Process proc = new Process(program, ramLocation, cpu.tick);
            cpu.switchContext(proc);

            // run a single program
            while (cpu.step())
                ;

            // show the output of the program
            dumpMemory(ramLocation + program.outputStart(), ramLocation + program.outputStart() + program.outputLength());
            System.out.println(proc);
            System.out.println();
            ramLocation += 5;
        }
    }

    private static void tickTock() throws Exception {
        CPU cpu = new CPU();

        Program prog1 = programs.get(0);
        Program prog2 = programs.get(1);

        System.arraycopy(disk, prog1.diskLocation(), ram, prog1.diskLocation(), prog1.totalLength());
        System.arraycopy(disk, prog2.diskLocation(), ram, prog2.diskLocation(), prog2.totalLength());

        Process p1 = new Process(prog1, 0, cpu.tick);
        Process p2 = new Process(prog2, prog1.totalLength(), cpu.tick);

        cpu.switchContext(p1);
        int current = 1;

        int numRunning = 2;
        while (numRunning != 0) {
            int proc = (int) (cpu.tick + 1) % 3;
            System.out.print("n="+numRunning + " c=" + current + "      ");
            boolean running = cpu.step();
            // swap every 3 instructions or on a HLT, but only if there are processes left
            if ((!running || proc == 0) && numRunning == 2) {
                System.out.println("swap");
                cpu.savePCB();
                if (current == 1) {
                    current = 2;
                    cpu.switchContext(p2);
                } else {
                    current = 1;
                    cpu.switchContext(p1);
                }
            }

            if (!running)
                numRunning--;
        }

        // should be e4, 55
        System.out.println(p1);
        dumpMemory(prog1.outputStart(), prog1.outputStart() + 1);
        System.out.println(p2);
        dumpMemory(prog2.diskLocation() + prog2.outputStart(), prog2.diskLocation() + prog2.outputStart() + 1);
    }

    public static final boolean PRINT_INSTRUCTION = true;

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
