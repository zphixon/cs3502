import java.util.Arrays;

public class CPU {
    public static final int NUM_REGISTERS = 16;
    int[] registers = new int[NUM_REGISTERS];
    int ip = 0;
    Process currentProcess;

    public void switchContext(Process newProcess) {
        currentProcess = newProcess;
        ip = newProcess.ramLocation;
    }

    public void savePCB() {
        for (int i = 0; i < NUM_REGISTERS; i++) {
            currentProcess.registers[i] = registers[i];
        }
        currentProcess.ip = ip;
    }

    // do one step of the cpu
    public boolean step() throws Exception {
        // decode the instruction
        // TODO memory address translation
        Instruction instruction = new Instruction(Device.ram[ip]);
        System.out.println(String.format("%04x  ", ip) + instruction);
        switch (instruction.opcode()) {
            // read content from *r2 or *address into r1
            case RD -> {
                // TODO cache, memory address translation, preemption
                int address;
                if (instruction.ioReg2() != 0)
                    address = registers[instruction.ioReg2()] / 4;
                else if (instruction.address() != 0)
                    address = instruction.address() / 4;
                else
                    throw new Exception("illegal instruction '" + instruction + "'");

                int data = Device.ram[address];
                registers[instruction.ioReg1()] = data;
            }

            // write content of r1 into *r2 or *address
            case WR -> {
                // TODO preemption
                int address;
                if (instruction.ioReg2() != 0)
                    address = registers[instruction.ioReg2()] / 4;
                else if (instruction.address() != 0)
                    address = instruction.address() / 4;
                else
                    throw new Exception("illegal instruction '" + instruction + "'");

                // TODO cache, memory address translation
                Device.ram[address] = registers[instruction.ioReg1()];
            }

            // store register data to memory
            case ST -> {
                int data = registers[instruction.b()];
                int address = registers[instruction.d()] / 4;
                // TODO cache, memory address translation
                Device.ram[address] = data;
            }

            // load memory into register with offset value in register
            case LW -> {
                int address = (instruction.address() + registers[instruction.b()]) / 4;
                // TODO cache, memory address translation
                registers[instruction.d()] = Device.ram[address];
            }

            // copy register value to another
            case MOV -> {
                registers[instruction.s1()] = registers[instruction.s2()];
            }

            case ADD -> {
                int a = registers[instruction.s1()];
                int b = registers[instruction.s2()];
                registers[instruction.d()] = a + b;
            }

            case SUB -> {
                int a = registers[instruction.s1()];
                int b = registers[instruction.s2()];
                registers[instruction.d()] = a - b;
            }

            case MUL -> {
                int a = registers[instruction.s1()];
                int b = registers[instruction.s2()];
                registers[instruction.d()] = a * b;
            }

            case DIV -> {
                int a = registers[instruction.s1()];
                int b = registers[instruction.s2()];
                registers[instruction.d()] = a / b;
            }

            case AND -> {
                int a = registers[instruction.s1()];
                int b = registers[instruction.s2()];
                registers[instruction.d()] = a & b;
            }

            case OR -> {
                int a = registers[instruction.s1()];
                int b = registers[instruction.s2()];
                registers[instruction.d()] = a | b;
            }

            // these are the same?
            case MOVI, LDI -> {
                registers[instruction.d()] = instruction.address();
            }

            case ADDI -> {
                registers[instruction.d()] += instruction.address();
            }

            case MULI -> {
                registers[instruction.d()] *= instruction.address();
            }

            case DIVI -> {
                registers[instruction.d()] /= instruction.address();
            }

            // set less than
            case SLT -> {
                int s1 = registers[instruction.s1()];
                int s2 = registers[instruction.s2()];

                if (s1 < s2)
                    registers[instruction.d()] = 1;
                else
                    registers[instruction.d()] = 0;
            }

            case SLTI -> {
                int b = registers[instruction.b()];
                int data = instruction.address() / 4;

                if (b < data)
                    registers[instruction.d()] = 1;
                else
                    registers[instruction.d()] = 0;
            }

            case HLT -> {
                savePCB();
                return false;
            }

            case NOP -> {
            }

            // branch instructions return because we don't want to increment the ip
            case JMP -> {
                // TODO memory address translation
                ip = instruction.address() / 4;
                return true;
            }

            case BEQ -> {
                int b = registers[instruction.b()];
                int d = registers[instruction.d()];

                // TODO memory address translation
                if (b == d) {
                    ip = instruction.address() / 4;
                    return true;
                }
            }

            case BNE -> {
                int b = registers[instruction.b()];
                int d = registers[instruction.d()];

                // TODO memory address translation
                if (b != d) {
                    ip = instruction.address() / 4;
                    return true;
                }
            }

            case BEZ -> {
                int b = registers[instruction.b()];

                // TODO memory address translation
                if (b == 0) {
                    ip = instruction.address() / 4;
                    return true;
                }
            }

            case BNZ -> {
                int b = registers[instruction.b()];

                // TODO memory address translation
                if (b != 0) {
                    ip = instruction.address() / 4;
                    return true;
                }
            }

            case BGZ -> {
                int b = registers[instruction.b()];

                // TODO memory address translation
                if (b > 0) {
                    ip = instruction.address() / 4;
                    return true;
                }
            }

            case BLZ -> {
                int b = registers[instruction.b()];

                // TODO memory address translation
                if (b < 0) {
                    ip = instruction.address() / 4;
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
