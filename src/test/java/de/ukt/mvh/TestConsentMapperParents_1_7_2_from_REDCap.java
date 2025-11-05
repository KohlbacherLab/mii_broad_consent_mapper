package de.ukt.mvh;

import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ca.uhn.fhir.context.FhirContext.forR4Cached;
import static org.junit.Assert.assertThrows;

public class TestConsentMapperParents_1_7_2_from_REDCap {
	
    private static Date birthday;

    @BeforeAll
    private static void init() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        birthday = dateFormat.parse("2020-05-13T00:00:00+02");
    }

/*    
    @Test
    public void testConsentMapper() throws Exception {
        String redCapExport = """
[
  {
    "psn": "TEST",
    "redcap_repeat_instrument": "",
    "redcap_repeat_instance": "",
    "datum_einwillig_forsch": "",
    "vers_einwillig_forsch": "",
    "bc_sb_1": "",
    "bc_sb_2": "",
    "bc_sb_3": "",
    "bc_sb_4": "",
    "bc_sb_5": "",
    "bc_sb_6": "",
    "bc_sb_7": "",
    "bc_sb_8": "",
    "bc_sb_9": "",
    "datum_einwillig_f_wid": "",
    "umfang_einwillig_f_wid": "",
    "forschungseinwilligungen_complete": ""
  },
  {
    "psn": "TEST",
    "redcap_repeat_instrument": "Forschungseinwilligungen",
    "redcap_repeat_instance": 1,
    "datum_einwillig_forsch": "2025-06-27",
    "vers_einwillig_forsch": "Kinder v.1.7.2",
    "bc_sb_1": "Yes",
    "bc_sb_2": "Yes",
    "bc_sb_3": "Yes",
    "bc_sb_4": "Yes",
    "bc_sb_5": "Yes",
    "bc_sb_6": "Yes",
    "bc_sb_7": "Yes",
    "bc_sb_8": "Yes",
    "bc_sb_9": "Yes",
    "datum_einwillig_f_wid": "",
    "umfang_einwillig_f_wid": "",
    "forschungseinwilligungen_complete": "Complete"
  }
]
""";
        Consent consent = ConsentMapperParents_1_7_2_from_REDCap.makeConsent(redCapExport,birthday);
        var jsonParser = forR4Cached().newJsonParser();

        ClassLoader classLoader = getClass().getClassLoader();
        var targetConsent = (Resource) jsonParser.parseResource(new FileReader(classLoader.getResource("consent_parents.json").getPath()));

        System.out.println(jsonParser.encodeResourceToString(targetConsent));
        System.out.println(jsonParser.encodeResourceToString(consent));

        Assertions.assertEquals(jsonParser.encodeResourceToString(targetConsent), jsonParser.encodeResourceToString(consent));
    }
*/

    @Test
    public void testWithdrawal() throws ParseException {
        String redCapExport = """
[
  {
    "psn": "TEST",
    "redcap_repeat_instrument": "",
    "redcap_repeat_instance": "",
    "datum_einwillig_forsch": "",
    "vers_einwillig_forsch": "",
    "bc_sb_1": "",
    "bc_sb_2": "",
    "bc_sb_3": "",
    "bc_sb_4": "",
    "bc_sb_5": "",
    "bc_sb_6": "",
    "bc_sb_7": "",
    "bc_sb_8": "",
    "bc_sb_9": "",
    "datum_einwillig_f_wid": "",
    "umfang_einwillig_f_wid": "",
    "forschungseinwilligungen_complete": ""
  },
  {
    "psn": "TEST",
    "redcap_repeat_instrument": "Forschungseinwilligungen",
    "redcap_repeat_instance": 1,
    "datum_einwillig_forsch": "2025-06-27",
    "vers_einwillig_forsch": "Kinder v.1.7.2",
    "bc_sb_1": "Yes",
    "bc_sb_2": "Yes",
    "bc_sb_3": "Yes",
    "bc_sb_4": "Yes",
    "bc_sb_5": "Yes",
    "bc_sb_6": "Yes",
    "bc_sb_7": "Yes",
    "bc_sb_8": "Yes",
    "bc_sb_9": "Yes",
    "datum_einwillig_f_wid": "2025-06-29",
    "umfang_einwillig_f_wid": "",
    "forschungseinwilligungen_complete": "Complete"
  }
]
""";
        Consent consent = ConsentMapperParents_1_7_2_from_REDCap.makeConsent(redCapExport,birthday);
        Assertions.assertEquals(0, consent.getProvision().getProvision().size());
    }

    @Test
    public void testComplete() throws ParseException {
        String redCapExport = """
[
  {
    "psn": "TEST",
    "redcap_repeat_instrument": "",
    "redcap_repeat_instance": "",
    "datum_einwillig_forsch": "",
    "vers_einwillig_forsch": "",
    "bc_sb_1": "",
    "bc_sb_2": "",
    "bc_sb_3": "",
    "bc_sb_4": "",
    "bc_sb_5": "",
    "bc_sb_6": "",
    "bc_sb_7": "",
    "bc_sb_8": "",
    "bc_sb_9": "",
    "datum_einwillig_f_wid": "",
    "umfang_einwillig_f_wid": "",
    "forschungseinwilligungen_complete": ""
  },
  {
    "psn": "TEST",
    "redcap_repeat_instrument": "Forschungseinwilligungen",
    "redcap_repeat_instance": 1,
    "datum_einwillig_forsch": "2025-06-27",
    "vers_einwillig_forsch": "Kinder v.1.7.2",
    "bc_sb_1": "Yes",
    "bc_sb_2": "Yes",
    "bc_sb_3": "Yes",
    "bc_sb_4": "Yes",
    "bc_sb_5": "Yes",
    "bc_sb_6": "Yes",
    "bc_sb_7": "Yes",
    "bc_sb_8": "Yes",
    "bc_sb_9": "Yes",
    "datum_einwillig_f_wid": "2025-06-29",
    "umfang_einwillig_f_wid": "",
    "forschungseinwilligungen_complete": "Incomplete"
  }
]
""";
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> {
                    Consent consent = ConsentMapperParents_1_7_2_from_REDCap.makeConsent(redCapExport,birthday);
                }
        );

        Assertions.assertEquals("REDCap form is not completed. Cannot generate FHIR Consent.", exception.getMessage());
    }
}
