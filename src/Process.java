import java.util.Arrays;

public class Process {
    Program program;
    int[] registers = new int[CPU.NUM_REGISTERS];
    int ip = 0;
    int ramLocation;
    int ioOperations = 0;
    int startTime;

    public Process(Program program, int ramLocation, int startTime) {
        this.program = program;
        this.ramLocation = ramLocation;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Process{" +
                "program=" + program.number() +
                ", registers=" + Arrays.toString(registers) +
                ", ip=" + ip +
                ", ramLocation=" + ramLocation +
                ", ioOperations=" + ioOperations +
                ", startTime=" + startTime +
                '}';
    }
}
