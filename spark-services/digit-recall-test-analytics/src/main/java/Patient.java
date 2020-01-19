public class Patient {

    private String patientId;
    private Float avgScore;
    private Float maxScore;
    private Float minScore;
    private Long avgInSeconds;
    private Long maxInSeconds;
    private Long minInSeconds;
    private int count;

    public Patient(String patientId, Float avgScore, Long avgInSeconds, int count) {
        this.patientId = patientId;
        this.avgScore = avgScore;
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

    public void setAvgScore(Float avgScore) {
        this.avgScore = avgScore;
    }

    public Float getAvgScore() {
        return avgScore;
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
