# broad_consent_mapper




## FHIR Mapper from the MI-I Broad Consent 1.7.2 

## Description
This package provides a command line tool to take answers from the MI-I Broad Consent version 1.7.2. and maps it into the core data set for consents of the MI-I. 
It supports custom configurations (leaving out specific questions for example retrospective data / sample use).

It also supports a mapping from a designated REDCap form, e.g. in  https://github.com/KohlbacherLab/nse-mv-genomseq. 

## Usage
For example you can run 

```de.ukt.mvh.CLIConsentMapperOlderMinors_1_7_2 --date_of_birth=12.12.2010 --date_of_consent=04.07.2025 -o outfileOlderMinors.json --consent_pat_data_1=true --consent_insurance_data_2=true --consent_contact_5=false```

## Support
Please file an issue in case of questions.
