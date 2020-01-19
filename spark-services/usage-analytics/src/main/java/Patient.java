import java.util.List;

public class Patient {

    public Patient(String patientId, List<PatientAnalytic> analytics) {
        this.patientId = patientId;
        this.analytics = analytics;
    }

    private String patientId;
    private List<PatientAnalytic> analytics;

}
