public class Patient {

    private String patientId;
    private Float avgRate;
    private Float maxScore;
    private Float minScore;
    private Long avgInSeconds;
    private Long maxInSeconds;
    private Long minInSeconds;
    private int count;

    public Patient(String patientId, Float avgRate, Long avgInSeconds, int count) {
        this.patientId = patientId;
        this.avgRate = avgRate;
        this.avgInSeconds = avgInSeconds;
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

    public void setAvgRate(Float avgRate) {
        this.avgRate = avgRate;
    }

    public Float getAvgRate() {
        return avgRate;
    }

    public Float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Float maxScore) {
        this.maxScore = maxScore;
    }

    public Float getMinScore() {
        return minScore;
    }

    public void setMinScore(Float minScore) {
        this.minScore = minScore;
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
}
