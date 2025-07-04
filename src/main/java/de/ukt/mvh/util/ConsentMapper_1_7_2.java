package de.ukt.mvh.util;

import org.hl7.fhir.r4.model.*;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.time.DateUtils.addYears;


public class ConsentMapper_1_7_2 {

    private static final String CONSENT_VERSION_1_7_2 = "urn:oid:2.16.840.1.113883.3.1937.777.24.2.2079";
    private static final String CONSENT_TYPE = "https://www.medizininformatik-initiative.de/fhir/modul-consent/StructureDefinition/mii-pr-consent-einwilligung";
    private static final String CONSENT_MII_CATEGORY_SYS = "https://www.medizininformatik-initiative.de/fhir/modul-consent/CodeSystem/mii-cs-consent-consent_category";
    private static final String CONSENT_LOINC_CATEGORY_SYS = "http://loinc.org";
    private static final String CONSENT_PROVISION_SYS = "urn:oid:2.16.840.1.113883.3.1937.777.24.5.3";

    private final String consentVersion;
    private final boolean forMinors;

    public ConsentMapper_1_7_2(String consentVersion, boolean forMinors) {
        this.consentVersion = consentVersion;
        this.forMinors = forMinors;
    }

    public ConsentMapper_1_7_2() {
        this.consentVersion = CONSENT_VERSION_1_7_2;
        this.forMinors = false;
    }

    public Consent makeConsent(Date date) {
        final CodeableConcept loincCode =
                new CodeableConcept(
                        new Coding(CONSENT_LOINC_CATEGORY_SYS, "57016-8", ""));

        final CodeableConcept miiCode =
                new CodeableConcept(
                        new Coding(CONSENT_MII_CATEGORY_SYS, "2.16.840.1.113883.3.1937.777.24.2.184", ""));
        final Coding scope = new Coding("http://terminology.hl7.org/CodeSystem/consentscope", "research", "");

        var consent = new Consent();
        consent.setMeta(new Meta().addProfile(CONSENT_TYPE));
        consent.setScope(new CodeableConcept(scope));
        consent.setDateTime(date);
        consent.setStatus(Consent.ConsentState.ACTIVE);
        consent.setPolicy(List.of(
                        new Consent.ConsentPolicyComponent().setUri(this.consentVersion)));
        consent.setCategory(Arrays.asList(loincCode, miiCode));
        return consent;
    }

    protected static Consent.provisionComponent createProvisionComponent(
            final String aCode, final String toDisplay, final Period period, final Consent.ConsentProvisionType type) {
        Consent.provisionComponent provisionComponent = new Consent.provisionComponent();
        provisionComponent.setType(type);
        provisionComponent.setPeriod(period);
        Coding code = new Coding(CONSENT_PROVISION_SYS, aCode, toDisplay);
        provisionComponent.setCode(List.of(new CodeableConcept(code)));
        return provisionComponent;
    }

    public Consent.provisionComponent makeProvisions(
            Date dateConsent,
            Date birthday,
            @Nullable Boolean consentPatDataProsp13,
            @Nullable Boolean consentPatDataRetro13,
            @Nullable Boolean consentInsuranceDataRetro21,
            @Nullable Boolean consentInsuranceDataProsp22,
            @Nullable Boolean consentBioSamples33,
            @Nullable Boolean consentBioSamplesAddl33,
            @Nullable Boolean consentBioSamplesRetro33,
            @Nullable Boolean consentContact41,
            @Nullable Boolean consentContactFindings42,
            @Nullable Boolean consentPatDataNonEU13,
            @Nullable Boolean consentBioSamplesNonEU33) {
        Period periodLong = new Period().setStart(dateConsent).setEnd(addYears(birthday, 18));

        if (this.forMinors && ChronoUnit.YEARS.between(
                dateConsent.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate(),
                birthday.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()) > 18) {
            throw new RuntimeException("Consent for minors was signed after 18th birthday."
                    + birthday.toString() + " vs consent " + dateConsent.toString());
        }

        Date after5years = addYears(dateConsent, 5);
        Period periodShort = new Period().setStart(dateConsent).setEnd(
                periodLong.getEnd().before(after5years) ? periodLong.getEnd() : after5years);

        return makeProvisionsWithPeriods(
                periodShort, periodLong, consentPatDataProsp13, consentPatDataRetro13, consentInsuranceDataRetro21,
                consentInsuranceDataProsp22, consentBioSamples33, consentBioSamplesAddl33, consentBioSamplesRetro33,
                consentContact41, consentContactFindings42, consentPatDataNonEU13, consentBioSamplesNonEU33
        );
    }

    public Consent.provisionComponent makeProvisionsWithPeriods(
            Period periodShort,
            Period periodLong,
            @Nullable Boolean consentPatDataProsp13,
            @Nullable Boolean consentPatDataRetro13,
            @Nullable Boolean consentInsuranceDataRetro21,
            @Nullable Boolean consentInsuranceDataProsp22,
            @Nullable Boolean consentBioSamples33,
            @Nullable Boolean consentBioSamplesAddl33,
            @Nullable Boolean consentBioSamplesRetro33,
            @Nullable Boolean consentContact41,
            @Nullable Boolean consentContactFindings42,
            @Nullable Boolean consentPatDataNonEU13,
            @Nullable Boolean consentBioSamplesNonEU33) {


        // Add provisions:
        Consent.provisionComponent provisionComponent = new Consent.provisionComponent();
        provisionComponent.setType(Consent.ConsentProvisionType.DENY);
        provisionComponent.setPeriod(periodLong);

        ArrayList<Consent.provisionComponent> provisions = new ArrayList<>(32);

        /* Ich willige ein in die Erhebung, Verarbeitung, Speicherung und wissenschaftliche Nutzung der
Patientendaten meines Kindes wie in Punkt 1.1 bis 1.3 der Einwilligungserklärung und Punkt 1 der
Elterninformation beschrieben. */
        if (consentPatDataProsp13 != null) {
            Consent.ConsentProvisionType consentPatDataProsp13Type = consentPatDataProsp13 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;

            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.2",
                    "IDAT erheben",
                    periodLong,
                    consentPatDataProsp13Type
            ));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.3",
                    "IDAT speichern, verarbeiten",
                    periodLong,
                    consentPatDataProsp13Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.4",
                    "IDAT zusammenfuehren Dritte",
                    periodLong,
                    consentPatDataProsp13Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.5",
                    "IDAT bereitstellen EU DSGVO NIVEAU",
                    periodLong,
                    consentPatDataProsp13Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.6",
                    "MDAT erheben",
                    periodShort,
                    consentPatDataProsp13Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.7",
                    "MDAT speichern, verarbeiten",
                    periodLong,
                    consentPatDataProsp13Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.8",
                    "MDAT wissenschaftlich nutzen EU DSGVO NIVEAU",
                    periodLong,
                    consentPatDataProsp13Type));

            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.9",
                    "MDAT zusammenfuehren Dritte",
                    periodLong,
                    consentPatDataProsp13Type));
        }

        /* Ich willige ein in die Verarbeitung und wissenschaftliche Nutzung der Patientendaten meines Kindes,
die im Rahmen früherer Behandlungen erhoben wurden, wie in Punkt 1.1 bis 1.3 der
Einwilligungserklärung und Punkt 1 der Elterninformation beschrieben. */
        if (consentPatDataRetro13 != null) {
            Consent.ConsentProvisionType consentPatDataRetro13Type = consentPatDataRetro13 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.45",
                    "MDAT retrospektiv speichern verarbeiten",
                    periodLong,
                    consentPatDataRetro13Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.46",
                    "MDAT retrospektiv wissenschaftlich nutzen EU DSGVO NIVEAU",
                    periodLong,
                    consentPatDataRetro13Type));

            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.47",
                    "MDAT retrospektiv zusammenfuehren Dritte",
                    periodLong,
                    consentPatDataRetro13Type));
        }
        /* Einmalig rückwirkend für die Daten der vergangenen 5 Kalenderjahre. Mit der dafür nötigen
Übermittlung der Krankenversicherungs-Nummer meines Kindes an das Universitätsklinikum Tübingen
bin ich einverstanden */
        if (consentInsuranceDataRetro21 != null) {
            Consent.ConsentProvisionType consentInsuranceDataRetro21Type = consentInsuranceDataRetro21 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.11",
                    "KKDAT 5J retrospektiv uebertragen",
                    periodLong,
                    consentInsuranceDataRetro21Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.12",
                    "KKDAT 5J retrospektiv speichern verarbeiten",
                    periodLong,
                    consentInsuranceDataRetro21Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.13",
                    "KKDAT 5J retrospektiv wissenschaftlich nutzen",
                    periodLong,
                    consentInsuranceDataRetro21Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.38",
                    "KKDAT 5J retrospektiv uebertragen KVNR",
                    periodLong,
                    consentInsuranceDataRetro21Type));
        }
        /* Für Daten ab dem Datum meiner Unterschrift über einen Zeitraum von 5 Jahren. Mit der dafür
nötigen Übermittlung der Krankenversicherungs-Nummer meines Kindes an das Universitätsklinikum
Tübingen bin ich einverstanden */
        if (consentInsuranceDataProsp22 != null) {
            Consent.ConsentProvisionType consentInsuranceDataProsp22Type = consentInsuranceDataProsp22 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.15",
                    "KKDAT 5J prospektiv uebertragen",
                    periodLong,
                    consentInsuranceDataProsp22Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.16",
                    "KKDAT 5J prospektiv speichern verarbeiten",
                    periodLong,
                    consentInsuranceDataProsp22Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.17",
                    "KKDAT 5J prospektiv wissenschaftlich nutzen",
                    periodLong,
                    consentInsuranceDataProsp22Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.39",
                    "KKDAT 5J prospektiv uebertragen KVNR",
                    periodLong,
                    consentInsuranceDataProsp22Type));
        }
        /* Ich willige ein in die Gewinnung, Lagerung und wissenschaftliche Nutzung der Bioproben (Gewebe und
Körperflüssigkeiten) meines Kindes, wie in Punkt 3.1 bis 3.3 der Einwilligungserklärung und Punkt 3 der
Elterninformation beschrieben.*/
        if (consentBioSamples33 != null) {
            Consent.ConsentProvisionType consentBioSamples33Type = consentBioSamples33 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.19",
                    "BIOMAT erheben",
                    periodShort,
                    consentBioSamples33Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.20",
                    "BIOMAT lagern verarbeiten",
                    periodLong,
                    consentBioSamples33Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.21",
                    "BIOMAT Eigentum übertragen",
                    periodShort,
                    consentBioSamples33Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.22",
                    "BIOMAT wissenschaftlich nutzen EU DSGVO NIVEAU",
                    periodLong,
                    consentBioSamples33Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.23",
                    "BIOMAT Analysedaten zusammenfuehren Dritte",
                    periodLong,
                    consentBioSamples33Type));
        }

        /* Meine Einwilligung umfasst auch die Entnahme geringer zusätzlicher Mengen von Bioproben bei einer
sowieso stattfindenden Routine-Blutentnahme oder –Punktion meines Kindes in den unter Punkt 3.2.
der Elterninformation beschriebenen Grenzen.*/
        if (consentBioSamplesAddl33 != null) {
            Consent.ConsentProvisionType consentBioSamplesAddl33Type = consentBioSamplesAddl33 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.25",
                    "BIOMAT Zusatzmengen entnehmen",
                    periodShort,
                    consentBioSamplesAddl33Type));
        }
        /* Ich willige ein in die Verarbeitung und wissenschaftliche Nutzung der Bioproben meines Kindes, die
im Rahmen früherer Behandlungen gewonnen wurden, wie in Punkt 3.1 bis 3.3 der Einwilligungs-
erklärung und Punkt 3 der Elterninformation beschrieben.*/
        if (consentBioSamplesRetro33 != null) {
            Consent.ConsentProvisionType consentBioSamplesRetro33Type = consentBioSamplesRetro33 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.51",
                    "BIOMAT retrospektiv lagern verarbeiten",
                    periodLong,
                    consentBioSamplesRetro33Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.52",
                    "BIOMAT retrospektiv wissenschaftlich nutzen EU DSGVO NIVEAU",
                    periodLong,
                    consentBioSamplesRetro33Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.53",
                    "BIOMAT retrospektiv Analysedaten zusammenfuehren Dritte",
                    periodLong,
                    consentBioSamplesRetro33Type));
        }

        /* Ich willige ein, dass ich vom Universitätsklinikum Tübingen erneut kontaktiert werden darf, um
gegebenenfalls zusätzliche für wissenschaftliche Fragen relevante Informationen oder Bioproben
meines Kindes zur Verfügung zu stellen, um über neue Forschungsvorhaben/Studien informiert zu
werden, und/oder um meine Einwilligung in die Verknüpfung der Patientendaten meines Kindes mit
medizinischen Informationen aus anderen Datenbanken einzuholen (siehe Punkt 4.1 der
Elterninformation).*/
        if (consentContact41 != null) {
            Consent.ConsentProvisionType consentContanct41Type = consentContact41 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.27",
                    "Rekontaktierung Verknüpfung Datenbanken",
                    periodLong,
                    consentContanct41Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.28",
                    "Rekontaktierung weitere Erhebung",
                    periodLong,
                    consentContanct41Type));
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.29",
                    "Rekontaktierung weitere Studien",
                    periodLong,
                    consentContanct41Type));
        }
        /* Ich willige ein, dass ich vom Universitätsklinikum Tübingen wieder kontaktiert werden darf, um über
        medizinische Zusatzbefunde informiert zu werden (siehe Punkt 4.2 der Elterninformation). */
        if (consentContactFindings42 != null) {
            Consent.ConsentProvisionType consentContanctFindings42Type = consentContactFindings42 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.31",
                    "Rekontaktierung Zusatzbefund",
                    periodLong,
                    consentContanctFindings42Type));
            provisionComponent.setProvision(provisions);
        }

        /* Meine Einwilligung umfasst auch die Übermittlung meiner Patientendaten in Länder, bei denen von der
Europäischen Kommission kein angemessenes Datenschutzniveau festgestellt wurde. Über die
möglichen Risiken einer solchen Übermittlung bin ich aufgeklärt worden (Punkt 1.3 in der
Patienteninformation). */
        if (consentPatDataNonEU13 != null) {
            Consent.ConsentProvisionType consentPatDataNonEU13Type = consentPatDataNonEU13 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.49",
                    "MDAT bereitstellen non EU DSGVO NIVEAU",
                    periodLong,
                    consentPatDataNonEU13Type));
            provisionComponent.setProvision(provisions);
        }

        /* Meine Einwilligung umfasst auch die Weitergabe meiner Biomaterialien in Länder, bei denen von der
Europäischen Kommission kein angemessenes Datenschutzniveau festgestellt wurde. Über die
möglichen Risiken einer solchen Weitergabe bin ich aufgeklärt worden (Punkt 1.3 in der
Patienteninformation). */
        if (consentBioSamplesNonEU33 != null) {
            Consent.ConsentProvisionType consentBioSamplesNonEU33Type = consentBioSamplesNonEU33 ? Consent.ConsentProvisionType.PERMIT : Consent.ConsentProvisionType.DENY;
            provisions.add(createProvisionComponent(
                    "2.16.840.1.113883.3.1937.777.24.5.3.55",
                    "BIOMAT bereitstellen ohne EU DSGVO NIVEAU",
                    periodLong,
                    consentBioSamplesNonEU33Type));
            provisionComponent.setProvision(provisions);
        }

        /* Unabhängig davon kann eine Kontaktaufnahme erfolgen, um Ihrem Kind über den behandelnden Arzt
         oder Hausarzt eine Rückmeldung über Analyseergebnisse zu geben, die für Ihr Kind von erheblicher
         Bedeutung sein könnten (siehe oben Punkt 1.5). */
        provisions.add(createProvisionComponent(
                "2.16.840.1.113883.3.1937.777.24.5.3.37",
                "Rekontaktierung Ergebnisse erheblicher Bedeutung",
                periodLong,
                Consent.ConsentProvisionType.PERMIT));
        provisionComponent.setProvision(provisions);
        return provisionComponent;
    }
}