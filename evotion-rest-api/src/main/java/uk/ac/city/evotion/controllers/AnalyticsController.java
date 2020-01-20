package uk.ac.city.evotion.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.city.evotion.utilities.EvotionContollerUtilities;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/rest/api/analytics")
public class AnalyticsController {

    @Autowired
    EvotionContollerUtilities Utilities;

    @RequestMapping(value = "/usage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUsageAnalyticsForPatients(
            @RequestParam(name = "period") String period,
            @RequestParam(name = "ids", required = false) String ids,
            HttpServletRequest req)
            throws IOException, InterruptedException {

        return Utilities.runSparkJob(
                "usage-analytics",
                "evotion-usage-analytics", req);
    }

    @RequestMapping(value = "/auditory-training", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAuditoryTrainingAnalyticsForPatients(
            @RequestParam(name = "lang", required = false) String lang,
            @RequestParam(name = "ids", required = false) String ids,
            HttpServletRequest req) throws IOException, InterruptedException {

        return Utilities.runSparkJob(
                "auditory-training-analytics",
                "evotion-auditory-training-analytics", req);
    }

    @RequestMapping(value = "/user-pta-test-results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserPtaTestResultsAnalyticsForPatients(
            @RequestParam(name = "ids", required = false) String ids, HttpServletRequest req)
            throws IOException, InterruptedException {

        return Utilities.runSparkJob(
                "user-pta-test-result-analytics",
                "evotion-user-pta-test-result-analytics", req);
    }

    @RequestMapping(value = "/digit-recall-test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDigitRecallTestAnalyticsForPatients(
            @RequestParam(name = "lang", required = false) String lang,
            @RequestParam(name = "ids", required = false) String ids,
            HttpServletRequest req)
            throws IOException, InterruptedException {

        return Utilities.runSparkJob(
                "digit-recall-test-analytics",
                "evotion-digit-recall-test-analytics", req);
    }

    @RequestMapping(value = "/speech-in-babble-test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSpeechInBabbleAnalyticsForPatients(
            @RequestParam(name = "lang", required = false) String lang,
            @RequestParam(name = "ids", required = false) String ids,
            HttpServletRequest req)
            throws IOException, InterruptedException {

        return Utilities.runSparkJob(
                "speech-in-babble-test-analytics",
                "evotion-speech-in-babble-test-analytics", req);
    }

    @RequestMapping(value = "/ttsnihl-test-result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTtsnihlTestResultAnalyticsForPatients(
            @RequestParam(name = "ids", required = false) String ids, HttpServletRequest req)
            throws IOException, InterruptedException {

        return Utilities.runSparkJob(
                "ttsnihl-test-result-analytics",
                "evotion-ttsnihl-test-result-analytics", req);
    }
}