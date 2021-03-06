class Instruction {
    // use a long because java's bit-shift operators extend the sign
    private final long value;
    public final OpcodeKind opcodeKind;
    public final Opcode opcode;
    private final int reg1;
    private final int reg2;
    private final int reg3;
    private final int shortAddress;
    private final int longAddress;

    public Instruction(int value) {
        this.value = Integer.toUnsignedLong(value);

        int opcodeKind = (int) ((this.value & OPCODE_KIND_MASK) >> 30);
        this.opcodeKind = OpcodeKind.values()[opcodeKind];

        int opcode = (int) ((this.value & OPCODE_MASK) >> 24);
        this.opcode = Opcode.values()[opcode];

        this.reg1 = (int) ((this.value & REG1_MASK) >> 20);
        this.reg2 = (int) ((this.value & REG2_MASK) >> 16);
        this.reg3 = (int) ((this.value & REG3_MASK) >> 12);

        this.shortAddress = (int) this.value & SHORT_ADDRESS_MASK;
        this.longAddress = (int) this.value & LONG_ADDRESS_MASK;
    }

    public String formatValue() {
        return String.format("%08x", value);
    }

    // masks for instruction decoding
    public static final int OPCODE_KIND_MASK       = 0b11000000000000000000000000000000;
    public static final int OPCODE_MASK            = 0b00111111000000000000000000000000;
    public static final int LONG_ADDRESS_MASK      = 0b00000000111111111111111111111111;
    public static final int SHORT_ADDRESS_MASK     = 0b00000000000000001111111111111111;
    public static final int REG1_MASK              = 0b00000000111100000000000000000000;
    public static final int REG2_MASK              = 0b00000000000011110000000000000000;
    public static final int REG3_MASK              = 0b00000000000000001111000000000000;

    public enum OpcodeKind {
        Arithmetic,
        Condition,
        Jump,
        IO,
    }

    public enum Opcode {
        RD,
        WR,
        ST,
        LW,
        MOV,
        ADD,
        SUB,
        MUL,
        DIV,
        AND,
        OR,
        MOVI,
        ADDI,
        MULI,
        DIVI,
        LDI,
        SLT,
        SLTI,
        HLT,
        NOP,
        JMP,
        BEQ,
        BNE,
        BEZ,
        BNZ,
        BGZ,
        BLZ,
    }

    public int s1() throws Exception {
        if (opcodeKind != OpcodeKind.Arithmetic)
            throw new Exception("wrong opcode kind for s1 '" + opcodeKind + "'");
        return reg1;
    }

    public int s2() throws Exception {
        if (opcodeKind != OpcodeKind.Arithmetic)
            throw new Exception("wrong opcode kind for s2 '" + opcodeKind + "'");
        return reg2;
    }

    public int d() throws Exception {
        if (opcodeKind == OpcodeKind.Condition)
            return reg2;
        if (opcodeKind == OpcodeKind.Arithmetic)
            return reg3;
        throw new Exception("wrong opcode kind for d '" + opcodeKind + "'");
    }

    public int b() throws Exception {
        if (opcodeKind != OpcodeKind.Condition)
            throw new Exception("wrong opcode kind for b '" + opcodeKind + "'");
        return reg1;
    }

    public int address() throws Exception {
        if (opcodeKind == OpcodeKind.Condition || opcodeKind == OpcodeKind.IO)
            return shortAddress;
        if (opcodeKind == OpcodeKind.Jump)
            return longAddress;
        throw new Exception("wrong opcode kind for d '" + opcodeKind + "'");
    }

    public int ioReg1() throws Exception {
        if (opcodeKind != OpcodeKind.IO)
            throw new Exception("wrong opcode kind for ioReg1 '" + opcodeKind + "'");
        return reg1;
    }

    public int ioReg2() throws Exception {
        if (opcodeKind != OpcodeKind.IO)
            throw new Exception("wrong opcode kind for ioReg2 '" + opcodeKind + "'");
        return reg2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return value == that.value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(formatValue());
        sb.append(" ");
        sb.append(String.format("%1$4s", opcode));
        sb.append(" ");

        switch (opcodeKind) {
            case Arithmetic -> {
                try {
                    sb.append(s1());
                    sb.append(" ");
                    sb.append(s2());
                    sb.append(" ");
                    sb.append(d());
                } catch (Exception e) {
                    System.err.println("toString failed");
                    System.exit(1);
                }
            }

            case Condition -> {
                try {
                    sb.append(b());
                    sb.append(" ");
                    sb.append(d());
                    sb.append(" ");
                    sb.append(address());
                } catch (Exception e) {
                    System.err.println("toString failed");
                    System.exit(1);
                }
            }

            case Jump -> {
                try {
                    sb.append(address());
                } catch (Exception e) {
                    System.err.println("toString failed");
                    System.exit(1);
                }
            }

            case IO -> {
                try {
                    sb.append(ioReg1());
                    sb.append(" ");
                    sb.append(ioReg2());
                    sb.append(" ");
                    sb.append(address());
                } catch (Exception e) {
                    System.err.println("toString failed");
                    System.exit(1);
                }
            }
        }

        return sb.toString();
    }
}
