package de.ukt.mvh;

import de.ukt.mvh.util.ConsentMapper_12_to_17_1_7_2;
import org.apache.commons.cli.*;
import org.hl7.fhir.r4.model.Consent;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ca.uhn.fhir.context.FhirContext.forR4Cached;

public class CLIConsentMapperOlderMinors_1_7_2 {
    public static void main(String[] args) throws IOException, java.text.ParseException {
        Options options = new Options();

        Option dateConsent = new Option("c", "date_of_consent", true,
                "date consent was signed format must be DD.MM.YYYY");
        dateConsent.setRequired(true);
        options.addOption(dateConsent);

        Option dateBirth = new Option("b", "date_of_birth", true,
                "date minor was born format must be MM.YYYY - day is skipped for anonymization");
        dateBirth.setRequired(true);
        options.addOption(dateBirth);

        Option consentPatData1 = new Option("q1", "consent_pat_data_1", true,
                "Answer in section 1 using patient data for research.");
        options.addOption(consentPatData1);

        Option consentInsuranceData2 = new Option("q2", "consent_insurance_data_2", true,
                "Answer in section 2 about using health insurance data for research.");
        options.addOption(consentInsuranceData2);

        Option consentBioSamples3 = new Option("q3", "consent_bio_samples_3", true,
                "Answer in section 3 about using bio samples for research.");
        options.addOption(consentBioSamples3);

        Option consentBioSamplesAddl4 = new Option("q4", "consent_addl_bio_samples_4", true,
                "Answer in section 4 about collecting small extra amounts of bio samples for research.");
        options.addOption(consentBioSamplesAddl4);

        Option consentContact5 = new Option("q5", "consent_contact_5", true,
                "Answer in section 5 about being contacted again for more studies.");
        options.addOption(consentContact5);

        Option consentDataNonDSGVO = new Option("q10", "consent_data_non_dsgvo_1_3", true,
                "Answer in section 1.5 about sharing data with researchers in countries with less data protection.");
        options.addOption(consentDataNonDSGVO);

        Option consentSamplesNonDSGVO = new Option("q11", "consent_samples_non_dsgvo_3_3", true,
                "Answer in section 3.3 about sharing bio samples with researchers in countries with less data protection.");
        options.addOption(consentSamplesNonDSGVO);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Date consentDate = dateFormat.parse(cmd.getOptionValue("date_of_consent"));
        Date birthday = dateFormat.parse("01." + cmd.getOptionValue("date_of_birth"));
        String outputFilePath = cmd.getOptionValue("output");
        ConsentMapper_12_to_17_1_7_2 mapper = new ConsentMapper_12_to_17_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                Boolean.valueOf(cmd.getOptionValue("consent_pat_data_1")),
                Boolean.valueOf(cmd.getOptionValue("consent_insurance_data_2")),
                Boolean.valueOf(cmd.getOptionValue("consent_bio_samples_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_addl_bio_samples_4")),
                Boolean.valueOf(cmd.getOptionValue("consent_contact_5")),
                Boolean.valueOf(cmd.getOptionValue("consent_data_non_dsgvo_1_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_samples_non_dsgvo_3_3"))
        ));
        var jsonParser = forR4Cached().newJsonParser();
        jsonParser.encodeResourceToWriter(consent, new FileWriter(outputFilePath));
        System.out.printf(jsonParser.encodeResourceToString(consent));
    }
}
