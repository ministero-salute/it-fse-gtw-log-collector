package it.finanze.sanita.fse2.ms.gtw.logcollector.config;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class Constants {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Collections {

        public static final String LOG_CONTROL_COLLECTION_NAME = "log_collector";

        public static final String LOG_KPI_COLLECTION_NAME = "log_collector_kpi";

        
    }


	@NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Profile {
		
        public static final String TEST = "test";

        public static final String TEST_PREFIX = "test_";

        /**
         * Dev profile.
         */
        public static final String DEV = "dev";

        /**
         * Docker profile.
         */
        public static final String DOCKER = "docker";


    }

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class App {

		@NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Regex {

            public static final String ASL_REGEX = "^\\d{3}";
            public static final String PRIVATE_HOSPITAL = "^\\d{6}$";
            public static final String PRIVATE_SSN_HOSPITAL_REGEX = "^\\d{9}$";
            public static final String PRIVATE_DOCTOR = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
            public static final String MMG_PLS_REGEX = "^\\d{3}.*[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
            // public static final String COMPLEX_STRUCTURE_REGEX = "^\\d{3}.*[a-zA-Z0-9]+$";

            public static class Compiled {
                private Compiled() {}
                public static final Pattern COMPILED_ASL_REGEX = Pattern.compile(ASL_REGEX);
                public static final Pattern COMPILED_PRIVATE_HOSPITAL_REGEX = Pattern.compile(PRIVATE_HOSPITAL);
                public static final Pattern COMPILED_PRIVATE_SSN_HOSPITAL_REGEX = Pattern.compile(PRIVATE_SSN_HOSPITAL_REGEX);
                public static final Pattern COMPILED_PRIVATE_DOCTOR_REGEX = Pattern.compile(PRIVATE_DOCTOR);
                public static final Pattern COMPILED_MMG_PLS_REGEX = Pattern.compile(MMG_PLS_REGEX);
                // public static final Pattern COMPILED_COMPLEX_STRUCTURE = Pattern.compile(COMPLEX_STRUCTURE_REGEX);
            }
        }

		@NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Custom {
            public static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss.SSS";
        }
    }

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Mongo {
        public static class Query {

            public static final String UNKNOWN_WORKFLOW_ID = "UNKNOWN_WORKFLOW_ID";
            public static final String REGION = "op_issuer.region";
        }

        public static class Fields {
            public static final String ID = "_id";

            private Fields() {}
            public static final String LOG_TYPE = "log_type";
            public static final String MESSAGE = "message";
            public static final String OPERATION = "operation";
            public static final String OP_RESULT = "op_result";
            public static final String OP_TIMESTAMP_START = "op_timestamp_start";
            public static final String OP_TIMESTAMP_END = "op_timestamp_end";
            public static final String OP_ERROR = "op_error";
            public static final String OP_ERROR_DESCRIPTION = "op_error_description";
            public static final String OP_ISSUER = "op_issuer";
            public static final String OP_LOCALITY = "op_locality";
            public static final String OP_DOCUMENT_TYPE = "op_document_type";
            public static final String MICROSERVICE_NAME = "microservice_name";
            public static final String OP_ROLE = "op_role";
            public static final String OP_FISCAL_CODE = "op_fiscal_code";
            public static final String GATEWAY_NAME = "gateway_name";
            public static final String OP_WARNING = "op_warning";
            public static final String OP_WARNING_DESCRIPTION = "op_warning_description";
            public static final String OP_SUBJ_APPLICATION = "op_subj_application";
            public static final String OP_SUBJ_APPLICATION_ID = "op_application_id";
            public static final String OP_SUBJ_APPLICATION_VENDOR = "op_application_vendor";
            public static final String OP_SUBJ_APPLICATION_VERSION = "op_application_version";
            public static final String WORKFLOW_INSTANCE_ID = "workflow_instance_id";
            public static final String TYPE_ID_EXTENSION = "typeIdExtension";
            public static final String PROCESSED = "processed";
            public static final String ADMINISTRATIVE_REQUEST = "administrative_request";
            public static final String AUTHOR_INSTITUTION = "author_institution";
            public static final String IS_SSN = "is_ssn";
            public static final String REGISTRY = "registry";
            public static final String REGION = "region";
            public static final String COMPANY = "company";
            public static final String STRUCTURE = "structure";

            public static final String LOG_TYPE_KPI = "kpi-structured-log";
            public static final String LOG_TYPE_CONTROL = "control-structured-log";

        }
    }

}
