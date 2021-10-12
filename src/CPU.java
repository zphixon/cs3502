import java.util.Arrays;

public class CPU {
    public static final int NUM_REGISTERS = 16;
    int[] registers = new int[NUM_REGISTERS];
    int ip = 0;
    Process currentProcess;

    public void switchContext(Process newProcess) {
        this.currentProcess = newProcess;
        this.ip = newProcess.ramLocation;
    }

    public void savePCB() {
        for (int i = 0; i < NUM_REGISTERS; i++) {
            currentProcess.registers[i] = this.registers[i];
        }
        currentProcess.ip = this.ip;
    }

    // do one step of the cpu
    public boolean step() throws Exception {
        // decode the instruction
        // TODO memory address translation
        Instruction instruction = new Instruction(Device.ram[ip]);
        System.out.println(String.format("%04x  ", this.ip) + instruction);
        switch (instruction.opcode()) {
            // read content from *r2 or *address into r1
            case RD -> {
                // TODO cache, memory address translation, preemption
                int address;
                if (instruction.ioReg2() != 0)
                    address = this.registers[instruction.ioReg2()] / 4;
                else if (instruction.address() != 0)
                    address = instruction.address() / 4;
                else
                    throw new Exception("illegal instruction '" + instruction + "'");

                int data = Device.ram[address];
                this.registers[instruction.ioReg1()] = data;
            }

            // write content of r1 into *r2 or *address
            case WR -> {
                // TODO preemption
                int address;
                if (instruction.ioReg2() != 0)
                    address = this.registers[instruction.ioReg2()] / 4;
                else if (instruction.address() != 0)
                    address = instruction.address() / 4;
                else
                    throw new Exception("illegal instruction '" + instruction + "'");

                // TODO cache, memory address translation
                Device.ram[address] = this.registers[instruction.ioReg1()];
            }

            // store register data to memory
            case ST -> {
                int data = this.registers[instruction.b()];
                int address = this.registers[instruction.d()] / 4;
                // TODO cache, memory address translation
                Device.ram[address] = data;
            }

            // load memory into register with offset value in register
            case LW -> {
                int address = (instruction.address() + this.registers[instruction.b()]) / 4;
                // TODO cache, memory address translation
                this.registers[instruction.d()] = Device.ram[address];
            }

            // copy register value to another
            case MOV -> {
                this.registers[instruction.s1()] = this.registers[instruction.s2()];
            }

            case ADD -> {
                int a = this.registers[instruction.s1()];
                int b = this.registers[instruction.s2()];
                this.registers[instruction.d()] = a + b;
            }

            case SUB -> {
                int a = this.registers[instruction.s1()];
                int b = this.registers[instruction.s2()];
                this.registers[instruction.d()] = a - b;
            }

            case MUL -> {
                int a = this.registers[instruction.s1()];
                int b = this.registers[instruction.s2()];
                this.registers[instruction.d()] = a * b;
            }

            case DIV -> {
                int a = this.registers[instruction.s1()];
                int b = this.registers[instruction.s2()];
                this.registers[instruction.d()] = a / b;
            }

            case AND -> {
                int a = this.registers[instruction.s1()];
                int b = this.registers[instruction.s2()];
                this.registers[instruction.d()] = a & b;
            }

            case OR -> {
                int a = this.registers[instruction.s1()];
                int b = this.registers[instruction.s2()];
                this.registers[instruction.d()] = a | b;
            }

            // these are the same?
            case MOVI, LDI -> {
                this.registers[instruction.d()] = instruction.address();
            }

            case ADDI -> {
                this.registers[instruction.d()] += instruction.address();
            }

            case MULI -> {
                this.registers[instruction.d()] *= instruction.address();
            }

            case DIVI -> {
                this.registers[instruction.d()] /= instruction.address();
            }

            // set less than
            case SLT -> {
                int s1 = this.registers[instruction.s1()];
                int s2 = this.registers[instruction.s2()];

                if (s1 < s2)
                    this.registers[instruction.d()] = 1;
                else
                    this.registers[instruction.d()] = 0;
            }

            case SLTI -> {
                int b = this.registers[instruction.b()];
                int data = instruction.address() / 4;

                if (b < data)
                    this.registers[instruction.d()] = 1;
                else
                    this.registers[instruction.d()] = 0;
            }

            case HLT -> {
                this.savePCB();
                return false;
            }

            case NOP -> {
            }

            // branch instructions return because we don't want to increment the ip
            case JMP -> {
                // TODO memory address translation
                this.ip = instruction.address() / 4;
                return true;
            }

            case BEQ -> {
                int b = this.registers[instruction.b()];
                int d = this.registers[instruction.d()];

                // TODO memory address translation
                if (b == d) {
                    this.ip = instruction.address() / 4;
                    return true;
                }
            }

            case BNE -> {
                int b = this.registers[instruction.b()];
                int d = this.registers[instruction.d()];

                // TODO memory address translation
                if (b != d) {
                    this.ip = instruction.address() / 4;
                    return true;
                }
            }

            case BEZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b == 0) {
                    this.ip = instruction.address() / 4;
                    return true;
                }
            }

            case BNZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b != 0) {
                    this.ip = instruction.address() / 4;
                    return true;
                }
            }

            case BGZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b > 0) {
                    this.ip = instruction.address() / 4;
                    return true;
                }
            }

            case BLZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b < 0) {
                    this.ip = instruction.address() / 4;
                    return true;
                }
            }
        }

        ip++;
        return true;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "registers=" + Arrays.toString(registers) +
                ", ip=" + ip +
                ", process=" + currentProcess.program.number() +
                '}';
    }
}
