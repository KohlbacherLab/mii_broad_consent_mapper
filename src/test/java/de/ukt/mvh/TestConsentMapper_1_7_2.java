package de.ukt.mvh;

import de.ukt.mvh.util.ConsentMapperParents_1_7_2;
import de.ukt.mvh.util.ConsentMapper_12_to_17_1_7_2;
import de.ukt.mvh.util.ConsentMapper_1_7_2;
import de.ukt.mvh.util.ConsentMapper_7_to_11_1_7_2;
import org.hl7.fhir.r4.model.Consent;
import ca.uhn.fhir.parser.IParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.io.InputStreamReader;
import java.util.Date;
import static ca.uhn.fhir.context.FhirContext.forR4Cached;
import static org.apache.commons.lang3.time.DateUtils.addYears;


public class TestConsentMapper_1_7_2 {

    private static Consent targetConsent;
    private static Date consentDate;
    private static Date birthday;

    @BeforeAll
    public static void init() throws Exception {
       var classLoader = TestConsentMapper_1_7_2.class.getClassLoader();
       var jsonParser = forR4Cached().newJsonParser();
       targetConsent = jsonParser.parseResource(Consent.class, new InputStreamReader(classLoader.getResourceAsStream("consent.json")));
       consentDate = targetConsent.getDateTime();
       birthday = addYears(targetConsent.getProvision().getPeriod().getEnd(), -18);
    }

    @Test
    public void testConsentMapper() throws Exception {
        ConsentMapper_1_7_2 mapper = new ConsentMapper_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(
          mapper.makeProvisions(
            consentDate,
            birthday,
            true,true,true,true,true,true,true,true,true,null,null
          )
        );
        Assertions.assertTrue(consent.equalsDeep(targetConsent));
    }

    @Test
    public void testConsentMapperParents() throws Exception {
        ConsentMapperParents_1_7_2 mapper = new ConsentMapperParents_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                true, true, true,true,true,true,true,true,true, null, null));
        Assertions.assertEquals("urn:oid:2.16.840.1.113883.3.1937.777.24.2.3542", consent.getPolicy().getFirst().getUri());
    }

    @ParameterizedTest
    @CsvSource({"0,0,7", "1,8,10", "2,11,14", "3,15,18", "4,19,23", "5,24,24", "6,25,27", "7,28,30", "8,31,31", "9,32,32", "10,33,33"})
    public void testPermitDeny(int i, int provStart, int provEnd) {
        ConsentMapper_1_7_2 mapper = new ConsentMapper_1_7_2();
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                i == 0, i == 1, i == 2, i == 3, i == 4, i == 5, i == 6, i == 7, i == 8, i == 9, i == 10));
        Assertions.assertEquals(35, consent.getProvision().getProvision().size());
        for(int j = 0; j < consent.getProvision().getProvision().size() - 1; ++j){
            Consent.ConsentProvisionType val = (j >= provStart && j <= provEnd) ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            Assertions.assertEquals(val, consent.getProvision().getProvision().get(j).getType());
        }
        Assertions.assertEquals(Consent.ConsentProvisionType.PERMIT, consent.getProvision().getProvision().getLast().getType());
    }

    @Test
    public void testConsentMapperYoungMinors() throws Exception {
        ConsentMapper_7_to_11_1_7_2 mapper = new ConsentMapper_7_to_11_1_7_2(false, false, true, true, true, true);
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                true, true));
        Assertions.assertEquals(29, consent.getProvision().getProvision().size());
    }

    @Test
    public void testConsentMapperOlderMinors() throws Exception {
        ConsentMapper_12_to_17_1_7_2 mapper = new ConsentMapper_12_to_17_1_7_2(true, true);
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                true, true, false, false, true, null, null));
        Assertions.assertEquals(30, consent.getProvision().getProvision().size());
    }

    @ParameterizedTest
    @CsvSource({"0,0,7", "1,8,10", "2,11,14", "3,15,18", "4,19,23", "5,24,24", "6,25,27", "7,28,30", "8,31,31", "9,32,32", "10,33,33"})
    public void testNullArg(int i, int provStart, int provEnd) {
        ConsentMapper_12_to_17_1_7_2 mapper = new ConsentMapper_12_to_17_1_7_2(true, true);
        Consent consent = mapper.makeConsent(consentDate);
        consent.setProvision(mapper.makeProvisions(
                consentDate,
                birthday,
                i == 0 ? null : true, i == 1 ? null : true, i == 2 ? null : true, i == 3 ? null : true, i == 4 ? null : true, i == 5 ? null : true, i == 6 ? null : true, i == 7 ? null : true, i == 8 ? null : true, i == 9 ? null : true, i == 10 ? null : true));
        Assertions.assertEquals(35 - (provEnd - provStart + 1), consent.getProvision().getProvision().size());
    }
}
