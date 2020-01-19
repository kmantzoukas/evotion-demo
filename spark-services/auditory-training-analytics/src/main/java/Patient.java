public class Patient {

    private String patientId;
    private String avgInTime;
    private Long avgInSeconds;
    private Long min;
    private Long max;
    private Long diff;
    private int count;

    public Patient(String patientId, String avgInTime, Long avgInSeconds, Long min, Long max, Long diff, int count) {
        this.patientId = patientId;
        this.avgInTime = avgInTime;
        this.avgInSeconds = avgInSeconds;
        this.max = max;
        this.min = min;
        this.diff = diff;
        this.count = count;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAvgInTime() {
        return avgInTime;
    }

    public void setAvgInTime(String avgInTime) {
        this.avgInTime = avgInTime;
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

    public Long getMin() {
        return min;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMax() {
        return max;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getDiff() {
        return diff;
    }

    public void setDiff(Long diff) {
        this.diff = diff;
    }
}
