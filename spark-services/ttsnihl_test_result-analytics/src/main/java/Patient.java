public class Patient {

    private String patientId;
    private Long avgInSeconds;
    private Long maxInSeconds;
    private Long minInSeconds;
    private Long diff;
    private int count;

    public Patient(String patientId, Long avgInSeconds, Long minInSeconds, Long maxInSeconds, Long diff, int count) {
        this.patientId = patientId;
        this.avgInSeconds = avgInSeconds;
        this.minInSeconds = minInSeconds;
        this.maxInSeconds = maxInSeconds;
        this.diff = diff;
        this.count = count;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Long getAvgInSeconds() {
        return avgInSeconds;
    }

    public void setAvgInSeconds(Long avgInSeconds) {
        this.avgInSeconds = avgInSeconds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getMaxInSeconds() {
        return maxInSeconds;
    }

    public void setMaxInSeconds(Long maxInSeconds) {
        this.maxInSeconds = maxInSeconds;
    }

    public Long getMinInSeconds() {
        return minInSeconds;
    }

    public void setMinInSeconds(Long minInSeconds) {
        this.minInSeconds = minInSeconds;
    }

    public Long getDiff() {
        return diff;
    }

    public void setDiff(Long diff) {
        this.diff = diff;
    }
}
