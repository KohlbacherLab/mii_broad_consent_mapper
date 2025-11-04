package de.ukt.mvh;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ukt.mvh.util.ConsentMapperParents_1_7_2;
import org.apache.commons.cli.*;
import org.hl7.fhir.r4.model.Consent;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ca.uhn.fhir.context.FhirContext.forR4Cached;

public class ConsentMapperParents_1_7_2_from_REDCap {

    protected static Consent makeConsent(String redcapFormular, Date birthday) throws ParseException {
        JsonObject jsonObject = JsonParser.parseString(redcapFormular).getAsJsonArray().get(1).getAsJsonObject();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date consentDate = dateFormat.parse(jsonObject.get("datum_einwillig_forsch").getAsString());
        ConsentMapperParents_1_7_2 mapper = new ConsentMapperParents_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        if(!jsonObject.get("forschungseinwilligungen_complete").getAsString().contentEquals("Complete")) {
            throw new RuntimeException("REDCap form is not completed. Cannot generate FHIR Consent.");
        }
        if(jsonObject.get("datum_einwillig_f_wid").getAsString().contentEquals("")) {  // no withdrawal.
            consent.setProvision(mapper.makeProvisions(
                    consentDate,
                    birthday,
                    jsonObject.get("bc_sb_1").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_2").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_3").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_4").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_5").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_6").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_7").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_8").getAsString().contentEquals("Yes"),
                    jsonObject.get("bc_sb_9").getAsString().contentEquals("Yes"),
                    null, null
            ));
        }
        return consent;
    }


    public static void main(String[] args) throws IOException, java.text.ParseException {
        Options options = new Options();

        Option dateBirth = new Option("b", "date_of_birth", true,
                "date minor was born format must be MM.YYYY - day is skipped for anonymization");
        dateBirth.setRequired(true);
        options.addOption(dateBirth);

        Option consentREDCap = new Option("q", "redcap_formular", true,
                "REDCap SE Export.");
        options.addOption(consentREDCap);

        Option output = new Option("o", "output", true, "output file");
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
        Date birthday = dateFormat.parse("01." + cmd.getOptionValue("date_of_birth"));
        String outputFilePath = cmd.getOptionValue("output");
        String redcapFormularFile = cmd.getOptionValue("redcap_formular");
        String redcapFormular = Files.readString(Paths.get(redcapFormularFile), StandardCharsets.UTF_8);
        Consent consent = makeConsent(redcapFormular, birthday);
        var jsonParser = forR4Cached().newJsonParser();
        jsonParser.encodeResourceToWriter(consent, new FileWriter(outputFilePath));
        System.out.printf(jsonParser.encodeResourceToString(consent));
    }
}
