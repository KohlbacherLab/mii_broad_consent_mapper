package de.ukt.mvh;

import de.ukt.mvh.util.ConsentMapperParents_1_7_2;
import de.ukt.mvh.util.ConsentMapper_1_7_2;
import org.apache.commons.cli.*;
import org.hl7.fhir.r4.model.Consent;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ca.uhn.fhir.context.FhirContext.forR4Cached;

public class CLIConsentMapper_1_7_2 {

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

        Option forMinorsOpt = new Option("m", "for_minors", true,
                "Whether this consent refers to a minor and was filled out by the guardians.");
        dateBirth.setRequired(true);
        options.addOption(forMinorsOpt);

        Option consentPatDataProsp13 = new Option("q1", "consent_pat_data_prosp_1_3", true,
                "Answer in section 1.3 about prospectively collecting and using data for research.");
        options.addOption(consentPatDataProsp13);

        Option consentPatDataRetro13 = new Option("q2", "consent_pat_data_retro_1_3", true,
                "Answer in section 1.3 about using restrospective data for research.");
        options.addOption(consentPatDataRetro13);

        Option consentInsuranceDataRetro21 = new Option("q3", "consent_insurance_data_retro_2_1", true,
                "Answer in section 2.1 about using retrospective health insurance data for research.");
        options.addOption(consentInsuranceDataRetro21);

        Option consentInsuranceDataProsp22 = new Option("q4", "consent_insurance_data_prosp_2_2", true,
                "Answer in section 2.2 about using prospective health insurance data for research.");
        options.addOption(consentInsuranceDataProsp22);

        Option consentBioSamples33 = new Option("q5", "consent_bio_samples_3_3", true,
                "Answer in section 3.3 about using bio samples for research.");
        options.addOption(consentBioSamples33);

        Option consentBioSamplesAddl33 = new Option("q6", "consent_addl_bio_samples_3_3", true,
                "Answer in section 3.3 about collecting small extra amounts of bio samples for research.");
        options.addOption(consentBioSamplesAddl33);

        Option consentBioSamplesRetro33 = new Option("q7", "consent_bio_samples_retro_3_3", true,
                "Answer in section 3.3 about using retrospective bio samples for research.");
        options.addOption(consentBioSamplesRetro33);

        Option consentContanct41 = new Option("q8", "consent_contact_4_1", true,
                "Answer in section 4.1 about being contacted again for more data / studies.");
        options.addOption(consentContanct41);

        Option consentContanctFindings42 = new Option("q9", "consent_contact_findings_4_2", true,
                "Answer in section 4.2 about being contacted again in case of additional clinical findings.");
        options.addOption(consentContanctFindings42);

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
        boolean forMinors = Boolean.parseBoolean(cmd.getOptionValue("for_minors"));

        ConsentMapper_1_7_2 mapper = forMinors ? new ConsentMapperParents_1_7_2() : new ConsentMapper_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                Boolean.valueOf(cmd.getOptionValue("consent_pat_data_prosp_1_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_pat_data_retro_1_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_insurance_data_retro_2_1")),
                Boolean.valueOf(cmd.getOptionValue("consent_insurance_data_prosp_2_2")),
                Boolean.valueOf(cmd.getOptionValue("consent_bio_samples_3_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_addl_bio_samples_3_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_bio_samples_retro_3_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_contact_findings_4_2")),
                Boolean.valueOf(cmd.getOptionValue("consent_contact_4_1")),
                Boolean.valueOf(cmd.getOptionValue("consent_data_non_dsgvo_1_3")),
                Boolean.valueOf(cmd.getOptionValue("consent_samples_non_dsgvo_3_3"))
        ));
        var jsonParser = forR4Cached().newJsonParser();
        jsonParser.encodeResourceToWriter(consent, new FileWriter(outputFilePath));
        System.out.printf(jsonParser.encodeResourceToString(consent));
    }
}
