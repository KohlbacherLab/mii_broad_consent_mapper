package de.ukt.mvh.util;

import org.hl7.fhir.r4.model.Consent;

import javax.annotation.Nullable;
import java.util.Date;

public class ConsentMapper_7_to_11_1_7_2 extends ConsentMapper_1_7_2 {
    private static final String CONSENT_VERSION_7_to_11_1_7_2 = "urn:oid:2.16.840.1.113883.3.1937.777.24.2.3543";
    private final boolean includeNonDSGVOData;
    private final boolean includeNonDSGVOBioSamples;
    private final boolean includeRetrospectiveDataCollection;
    private final boolean includeInsuranceDataCollection;
    private final boolean includeAddlBioSamples;
    private final boolean includeRetrospectiveBioSamples;

    public ConsentMapper_7_to_11_1_7_2() {
        super(CONSENT_VERSION_7_to_11_1_7_2, true);
        includeNonDSGVOData = false;
        includeNonDSGVOBioSamples = false;
        includeRetrospectiveDataCollection = false;
        includeRetrospectiveBioSamples = false;
        includeInsuranceDataCollection = false;
        includeAddlBioSamples = false;
    }

    public ConsentMapper_7_to_11_1_7_2(boolean includeNonDSGVOData, boolean includeNonDSGVOBioSamples, boolean includeRetrospectiveDataCollection, boolean includeInsuranceDataCollection, boolean includeAddlBioSamples, boolean includeRetrospectiveBioSamples) {
        super(CONSENT_VERSION_7_to_11_1_7_2, true);
        this.includeNonDSGVOData = includeNonDSGVOData;
        this.includeNonDSGVOBioSamples = includeNonDSGVOBioSamples;
        this.includeRetrospectiveDataCollection = includeRetrospectiveDataCollection;
        this.includeInsuranceDataCollection = includeInsuranceDataCollection;
        this.includeAddlBioSamples = includeAddlBioSamples;
        this.includeRetrospectiveBioSamples = includeRetrospectiveBioSamples;
    }

    public Consent.provisionComponent makeProvisions(
            Date dateConsent,
            Date birthday,
            @Nullable Boolean consentPatData,
            @Nullable Boolean consentBioSamples) {
        return makeProvisions(
                dateConsent, birthday,
                consentPatData, this.includeRetrospectiveDataCollection ? consentPatData : null,
                this.includeInsuranceDataCollection ? consentPatData : null,
                this.includeInsuranceDataCollection ? consentPatData : null,
                consentBioSamples, this.includeAddlBioSamples ? consentBioSamples : null,
                this.includeRetrospectiveBioSamples ? consentBioSamples : null,
                null, null,
                this.includeNonDSGVOData ?  consentPatData : null,
                this.includeNonDSGVOBioSamples ? consentBioSamples : null);
    }
}
