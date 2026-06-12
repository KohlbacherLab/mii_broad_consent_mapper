package de.ukt.mvh;


import de.ukt.mvh.util.ConsentMapper_1_7_2;
import org.apache.commons.cli.*;
import org.hl7.fhir.r4.model.Consent;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ca.uhn.fhir.context.FhirContext.forR4Cached;

public class CLIRevokedConsentMapper_1_7_2 {

    public static void main(String[] args) throws IOException, java.text.ParseException {
        Options options = new Options();

        Option dateConsent = new Option("d", "date_of_consent", true,
                "date consent was signed format must be DD.MM.YYYY");
        dateConsent.setRequired(true);
        options.addOption(dateConsent);

        Option dateBirth = new Option("d", "date_of_birth", true,
                "date minor was born format must be MM.YYYY - day is skipped for anonymization");
        dateBirth.setRequired(true);
        options.addOption(dateBirth);

        Option forMinorsOpt = new Option("m", "for_minors", true,
                "Whether this consent refers to a minor and was filled out by the guardians.");
        dateBirth.setRequired(true);
        options.addOption(forMinorsOpt);

        Option consentPatDataRetro13 = new Option("q2", "incl_pat_data_retro_1_3", true,
                "Whether the retrospective data module was included in section 1.3.");
        options.addOption(consentPatDataRetro13);

        Option consentInsuranceData = new Option("q3", "incl_insurance_data", true,
                "Whether the module for health insurance data was included in section 2.1/2.2.");
        options.addOption(consentInsuranceData);

        Option consentBioSamples33 = new Option("q5", "incl_bio_samples_3_3", true,
                "Whether the bio sample module was included in section 3.3.");
        options.addOption(consentBioSamples33);

        Option consentBioSamplesAddl33 = new Option("q6", "incl_addl_bio_samples_3_3", true,
                "Whether the module for collecting small extra amounts of bio samples was included in section 3.3.");
        options.addOption(consentBioSamplesAddl33);

        Option consentBioSamplesRetro33 = new Option("q7", "incl_bio_samples_retro_3_3", true,
                "Whether the module for  retrospective bio samples was included in section 3.3.");
        options.addOption(consentBioSamplesRetro33);

        Option consentDataNonDSGVO = new Option("q10", "incl_data_non_dsgvo_1_3", true,
                "Whether the module for sharing data with researchers in countries with less data was included in section 1.5.");
        options.addOption(consentDataNonDSGVO);

        Option consentSamplesNonDSGVO = new Option("q11", "incl_samples_non_dsgvo_3_3", true,
                "Whether the module for sharing bio samples with researchers in countries with less data was included in section 3.3.");
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

        ConsentMapper_1_7_2 mapper = new ConsentMapper_1_7_2(ConsentMapper_1_7_2.CONSENT_REVOKED_VERSION_1_7_2, forMinors);
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.revokeProvisions(
                consentDate,
                birthday,
                cmd.hasOption("incl_pat_data_retro_1_3"),
                cmd.hasOption("incl_data_non_dsgvo_1_3"),
                cmd.hasOption("incl_insurance_data"),
                cmd.hasOption("incl_bio_samples_3_3"),
                cmd.hasOption("incl_addl_bio_samples_3_3"),
                cmd.hasOption("incl_bio_samples_retro_3_3"),
                cmd.hasOption("incl_samples_non_dsgvo_3_3")
        ));
        var jsonParser = forR4Cached().newJsonParser();
        jsonParser.encodeResourceToWriter(consent, new FileWriter(outputFilePath));
        System.out.printf(jsonParser.encodeResourceToString(consent));
    }
}

