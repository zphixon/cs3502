public class Process {
    int pc;
    int[] registers = new int[CPU.NUM_REGISTERS];
    Program program;
    int ramLocation;
}
