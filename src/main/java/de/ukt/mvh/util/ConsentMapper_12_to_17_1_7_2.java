package de.ukt.mvh.util;

import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Period;

import javax.annotation.Nullable;
import java.util.Date;

import static org.apache.commons.lang3.time.DateUtils.addYears;

public class ConsentMapper_12_to_17_1_7_2 extends ConsentMapper_1_7_2 {
    private static final String CONSENT_VERSION_12_to_17_1_7_2 = "urn:oid:2.16.840.1.113883.3.1937.777.24.2.3543";
    private final boolean includeRetrospectiveDataCollection;
    private final boolean includeRetrospectiveBioSamples;

    public ConsentMapper_12_to_17_1_7_2() {
        super(CONSENT_VERSION_12_to_17_1_7_2, true);
        includeRetrospectiveDataCollection = false;
        includeRetrospectiveBioSamples = false;
    }

    public ConsentMapper_12_to_17_1_7_2(boolean includeRetrospectiveDataCollection, boolean includeRetrospectiveBioSamples) {
        super(CONSENT_VERSION_12_to_17_1_7_2, true);
        this.includeRetrospectiveDataCollection = includeRetrospectiveDataCollection;
        this.includeRetrospectiveBioSamples = includeRetrospectiveBioSamples;
    }

    public Consent.provisionComponent makeProvisions(
            Date dateConsent,
            Date birthday,
            @Nullable Boolean consentPatData1,
            @Nullable Boolean consentInsuranceData2,
            @Nullable Boolean consentBioSamples3,
            @Nullable Boolean consentBioSamplesAddl4,
            @Nullable Boolean consentContact5,
            @Nullable Boolean consentPatDataNonEU13,
            @Nullable Boolean consentBioSamplesNonEU33) {
        Consent.provisionComponent provisionComponent = makeProvisions(
                dateConsent, birthday,
                consentPatData1, this.includeRetrospectiveDataCollection ? consentPatData1 : null,
                consentInsuranceData2,
                this.includeRetrospectiveDataCollection ? consentInsuranceData2 : null,
                consentBioSamples3, consentBioSamplesAddl4,
                this.includeRetrospectiveBioSamples ? consentBioSamples3 : null,
                null, // Contact41 is broader than Contact5 - so we skip it here and add it below
                null,
                consentPatDataNonEU13,
                consentBioSamplesNonEU33);

        if (consentContact5 != null) {
            Consent.ConsentProvisionType consentContact5Type = consentContact5 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisionComponent.getProvision().add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.29",
                    "Rekontaktierung weitere Studien",
                    new Period().setStart(dateConsent).setEnd(addYears(birthday, 18)),
                    consentContact5Type));
        }
        return provisionComponent;
    }
}
