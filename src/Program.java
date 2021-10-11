public record Program(int number, int length, int priority, int inputLength, int outputLength, int tempLength,
                      int diskLocation) {
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
