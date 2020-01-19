public class PatientAnalytic {

    public PatientAnalytic(String from, String to, String avg, String total) {
        this.from = from;
        this.to = to;
        this.avg = avg;
        this.total = total;
    }

    private String from;
    private String to;
    private String avg;
    private String total;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
