import java.util.ArrayList;

public class Device {
    public static void main(String[] args) throws Exception {
        Loader.load("programs.txt");
        dumpDisk();

        for (Program prog : programs) {
            System.out.println(prog);
            for (int i = prog.diskLocation(); i < prog.diskLocation() + prog.length(); i++) {
                System.out.println(new Instruction(disk[i]));
            }
        }
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
        int i = 1;
        for (int byte_ : disk) {
            System.out.print(String.format("    %08x", byte_));

            if (i % 16 == 0)
                System.out.println();

            i++;
        }
    }
}
