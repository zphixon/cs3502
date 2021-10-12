public class CPU {
    public static final int NUM_REGISTERS = 16;
    int[] registers = new int[NUM_REGISTERS];
    int ip = 0;

    public void step() throws Exception {
        Instruction instruction = new Instruction(Device.ram[ip]);
        System.out.println(String.format("%04x  ", this.ip) + instruction);
        switch (instruction.opcode()) {
            // read content from *r2 or *address into r1
            case RD -> {
                // TODO cache, memory address translation
                int data;
                if (instruction.ioReg2() != 0)
                    data = Device.ram[instruction.ioReg2()/4];
                else if (instruction.address() != 0)
                    data = Device.ram[instruction.address()/4];
                else
                    throw new Exception("illegal instruction '" + instruction + "'");

                this.registers[instruction.ioReg1()] = data;
            }

            // write content of r1 into *r2 or *address
            case WR -> {
                int address;
                if (instruction.ioReg2() != 0)
                    address = this.registers[instruction.ioReg2()];
                else if (instruction.address() != 0)
                    address = instruction.address();
                else
                    throw new Exception("illegal instruction '" + instruction + "'");

                // TODO cache, memory address translation
                Device.ram[address/4] = this.registers[instruction.ioReg1()];
            }

            case ST -> {
                int data = this.registers[instruction.b()];
                int address = this.registers[instruction.d()];
                // TODO cache, memory address translation
                Device.ram[address/4] = data;
            }

            case LW -> {
                int address = this.registers[instruction.b()];
                // TODO cache, memory address translation
                this.registers[instruction.d()] = Device.ram[address/4];
            }

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
                int d = this.registers[instruction.d()];

                if (b < d)
                    this.registers[instruction.d()] = 1;
                else
                    this.registers[instruction.d()] = 0;
            }

            case HLT -> {
                // context switch
            }

            case NOP -> {}

            case JMP -> {
                // TODO memory address translation
                this.ip = instruction.address();
            }

            case BEQ -> {
                int b = this.registers[instruction.b()];
                int d = this.registers[instruction.d()];

                // TODO memory address translation
                if (b == d)
                    this.ip = instruction.address();
            }

            case BNE -> {
                int b = this.registers[instruction.b()];
                int d = this.registers[instruction.d()];

                // TODO memory address translation
                if (b != d)
                    this.ip = instruction.address();
            }

            case BEZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b == 0)
                    this.ip = instruction.address();
            }

            case BNZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b != 0)
                    this.ip = instruction.address();
            }

            case BGZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b > 0)
                    this.ip = instruction.address();
            }

            case BLZ -> {
                int b = this.registers[instruction.b()];

                // TODO memory address translation
                if (b < 0)
                    this.ip = instruction.address();
            }
        }
        ip++;
    }
}
