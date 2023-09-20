package org.eclipse.tractusx.traceability.infrastructure.edc.properties;

import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EdcPropertiesTest extends IntegrationTestSpecification {

    @Autowired
    EdcProperties edcProperties;

    @Test
    void test() {
        assertThat(edcProperties).isNotNull();
    }

}
