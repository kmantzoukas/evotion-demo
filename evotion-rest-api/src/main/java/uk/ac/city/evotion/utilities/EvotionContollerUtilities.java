package uk.ac.city.evotion.utilities;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import uk.ac.city.evotion.entities.CreateSubmissionResponse;
import uk.ac.city.evotion.entities.Results;
import uk.ac.city.evotion.entities.SubmissionStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EvotionContollerUtilities {

    @Value("${spring.spark.master.host}")
    private String sparkMaster;

    @Value("${spring.spark.master.port}")
    private int sparkMasterPort;

    @Value("${spring.spark.submit.url}")
    private String url;

    @Value("${spring.spark.service.timeout}")
    private int timeout;

    @Value("${sping.hdfs.namenode.host}")
    private String hdfsNamenodeHost;

    @Value("${sping.hdfs.namenode.port}")
    private int hdfsNamenodePort;

    @Autowired
    VelocityEngine engine;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    JdbcTemplate template;

    public String runSparkJob(String jar, String appName, HttpServletRequest req) throws IOException, InterruptedException {
         /*
        Generate a random requestId for the specific service invocation
         */
        String requestId = RandomStringUtils.random(16, true, true).toLowerCase();

        /*
        Create a request to submit as a Spark job on the master node
         */
        HttpEntity<String> request = createSparkSubmitRequest(jar, appName, req.getParameterMap(), requestId);

        /*
        Submit the created Spark job via Spark's REST API and collect the relevant submission id
         */
        String submissionId = restTemplate.postForObject(url, request, CreateSubmissionResponse.class).getSubmissionId();

        /*
        Collect and return the results of the Spark job stored into the Evotion database
         */
        return getServiceResults(requestId, submissionId);
    }

    private HttpEntity<String> createSparkSubmitRequest(String jar, String appName, Map<String, String[]> parameters, String requestId) throws IOException {
        VelocityContext context = new VelocityContext();
        Template template = engine.getTemplate("/templates/spark-submision-template.vm");
        context.put("spark-arguments", extractParameters(parameters, requestId));
        context.put("spark-master-host", sparkMaster);
        context.put("spark-master-port", sparkMasterPort);

        context.put("hdfs-namenode-host", hdfsNamenodeHost);
        context.put("hdfs-namenode-port", hdfsNamenodePort);

        context.put("spark-service-name", appName);
        context.put("spark-service-jar", jar);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity(writer.toString(), headers);

        return request;
    }

    private String[] extractParameters(Map<String, String[]> parameters, String requestId) {

        List<String> buffer = new ArrayList(0);
        buffer.add(requestId);

        for(Map.Entry<String, String[]> parameter : parameters.entrySet()){
            buffer.add(parameter.getValue()[0]);
        }

        return buffer.toArray(new String[0]);
    }

    private String getServiceResults(String requestId, String submissionId) throws InterruptedException {

        int retries = 0;

        SubmissionStatus submissionStatus =  restTemplate
                .getForObject(String.format(
                        "http://%s:%d/v1/submissions/status/%s",
                        sparkMaster,
                        sparkMasterPort,
                        submissionId),
                        SubmissionStatus.class);

        /*
        While the service hasn't finished or hasn't failed keep waiting for the results. If the waiting time goes beyond what is predetermined as
        a timeout value then exit the while loop. As soon as the service has completed the results are returned. The default timeout value is 60 seconds.
         */
        while ( !("FINISHED".equals(submissionStatus.getDriverState()) && retries < timeout && !("FAILED".equals(submissionStatus.getDriverState())) )){
            Thread.sleep(1000);

            submissionStatus = restTemplate
                    .getForObject(String.format(
                            "http://%s:%d/v1/submissions/status/%s",
                            sparkMaster,
                            sparkMasterPort,
                            submissionId),
                            SubmissionStatus.class);
            retries++;
        }

        String result =  template.queryForObject(
                "SELECT * FROM evotion.results WHERE requestId = ?",
                new String[]{requestId},
                (rs, rowNum) ->
                        new Results(
                                rs.getInt("id"),
                                rs.getString("requestId"),
                                rs.getString("result")
                        )).getResult();

        template.update("DELETE FROM evotion.results WHERE requestId =?", new String[]{requestId});

        return result;
    }
}
