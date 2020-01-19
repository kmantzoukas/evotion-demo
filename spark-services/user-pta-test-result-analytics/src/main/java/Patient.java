public class Patient {

    private String patientId;
    private Float average;
    private Float min;
    private Float max;
    private Float diff;
    private int count;

    public Patient(String patientId, Float average, Float min, Float max, Float diff, int count) {
        this.patientId = patientId;
        this.average = average;
        this.min = min;
        this.max = max;
        this.diff = diff;
        this.count = count;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        this.min = min;
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        this.max = max;
    }

    public Float getDiff() {
        return diff;
    }

    public void setDiff(Float diff) {
        this.diff = diff;
    }
}
