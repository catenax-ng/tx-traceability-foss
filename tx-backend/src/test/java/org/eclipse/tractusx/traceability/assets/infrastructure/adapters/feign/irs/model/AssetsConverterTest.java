package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AssetsConverterTest {

    @Test
    void test() throws IOException {
         ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream file = AssetsConverter.class.getResourceAsStream("/data/irs_assets_v2_singleUsageAsBuilt_and_assemblyPartRelationship.json");
        JobResponse response = mapper.readValue(file, JobResponse.class);
        JobResponse respon1se = mapper.readValue(file, JobResponse.class);
    }

}
