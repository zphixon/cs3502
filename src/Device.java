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
        if (program1.totalLength() >= 0)
            System.arraycopy(disk, 0, ram, 0, program1.totalLength());

        CPU cpu = new CPU();
        while (cpu.step())
            ;
        System.out.println(ram[program1.length() + program1.inputLength()]);
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
