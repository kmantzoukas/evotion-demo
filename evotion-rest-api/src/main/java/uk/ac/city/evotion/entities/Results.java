package uk.ac.city.evotion.entities;

public class Results {

    private int id;
    private String requestId;
    private String result;

    public Results(int id, String requestId, String result) {
        this.id = id;
        this.requestId = requestId;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
