public record Program(int number, int length, int priority, int inputLength, int outputLength, int tempLength,
                      int diskLocation) {
    public int totalLength() {
        return length + inputLength + outputLength + tempLength;
    }

    public int inputStart() {
        return length;
    }

    public int outputStart() {
        return length + inputLength;
    }

    public int tempStart() {
        return length + inputLength + outputLength;
    }

    @Override
    public String toString() {
        return "Program{" +
                "number=" + number +
                ", length=" + length +
                ", priority=" + priority +
                ", inputLength=" + inputLength +
                ", outputLength=" + outputLength +
                ", tempLength=" + tempLength +
                ", diskLocation=" + diskLocation +
                '}';
    }
}
