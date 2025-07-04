package de.ukt.mvh;

import de.ukt.mvh.util.ConsentMapper_7_to_11_1_7_2;
import org.apache.commons.cli.*;
import org.hl7.fhir.r4.model.Consent;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ca.uhn.fhir.context.FhirContext.forR4Cached;

public class CLIConsentMapperYoungMinors_1_7_2 {

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


        Option consentPatData = new Option("q1", "consent_pat_data", true,
                "Answer in section 1 about sharing data for research.");
        options.addOption(consentPatData);

        Option consentBioSamples = new Option("q2", "consent_bio_samples", true,
                "Answer in section 1 about sharing bio samples for research.");
        options.addOption(consentBioSamples);

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
        ConsentMapper_7_to_11_1_7_2 mapper = new ConsentMapper_7_to_11_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                Boolean.valueOf(cmd.getOptionValue("consent_pat_data")),
                Boolean.valueOf(cmd.getOptionValue("consent_pat_bio_samples"))
        ));
        var jsonParser = forR4Cached().newJsonParser();
        jsonParser.encodeResourceToWriter(consent, new FileWriter(outputFilePath));
        System.out.printf(jsonParser.encodeResourceToString(consent));
    }
}
