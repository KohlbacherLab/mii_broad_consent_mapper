package de.ukt.mvh;

import org.hl7.fhir.r4.model.Consent;
import ca.uhn.fhir.parser.IParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import static ca.uhn.fhir.context.FhirContext.forR4Cached;
import static org.apache.commons.lang3.time.DateUtils.addYears;
import static org.junit.Assert.assertThrows;

public class TestConsentMapperParents_1_7_2_from_REDCap {
	
    private static Consent targetConsent;
    private static Date birthday;

    @BeforeAll
    public static void init() throws Exception {
       var classLoader = TestConsentMapper_1_7_2.class.getClassLoader();
       var jsonParser = forR4Cached().newJsonParser();
       targetConsent = jsonParser.parseResource(Consent.class, new InputStreamReader(classLoader.getResourceAsStream("consent_parents.json")));
       birthday = addYears(targetConsent.getProvision().getPeriod().getEnd(), -18);
    }


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
        Assertions.assertTrue(consent.equalsDeep(targetConsent));
    }

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
