import java.util.ArrayList;

public class Device {
    public static void main(String[] args) throws Exception {
        Loader.load("programs.txt");
        dumpDisk();

//        for (Program program : programs) {
//            for (int i = program.diskLocation(); i < program.length() + program.diskLocation(); i++) {
//                Instruction instruction = new Instruction(disk[i]);
//                if ((instruction.opcodeKind() == Instruction.OpcodeKind.Condition || instruction.opcodeKind() == Instruction.OpcodeKind.IO)
//                && instruction.opcode() != Instruction.Opcode.ADDI && instruction.opcode() != Instruction.Opcode.MOVI) {
//                    System.out.println(instruction + " -> " + Device.disk[program.diskLocation() + (instruction.address() / 4)]);
//                }
//            }
//        }

        Program program1 = programs.get(0);
        for (int i = 0; i < program1.totalLength(); i++)
            ram[i] = disk[i];

        CPU cpu = new CPU();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        cpu.step();
        dumpMemory(program1.totalLength());
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
        dumpDisk(DISK_WORDS);
    }

    public static void dumpDisk(int upTo) {
        int i = 1;
        for (int word : disk) {
            System.out.print(String.format("    %08x", word));

            if (i % 16 == 0)
                System.out.println();

            i++;
            if (i == upTo)
                return;
        }
    }

    public static void dumpMemory() {
        dumpMemory(RAM_WORDS);
    }

    public static void dumpMemory(int upTo) {
        int i = 1;
        for (int word : ram) {
            System.out.print(String.format("    %08x", word));

            if (i % 16 == 0)
                System.out.println();

            i++;
            if (i == upTo)
                return;
        }
    }
}
