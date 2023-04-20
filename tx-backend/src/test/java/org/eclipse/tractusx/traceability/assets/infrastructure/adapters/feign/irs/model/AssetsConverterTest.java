package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.traceability.TraceabilityApplication;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetsConverterTest {

    @InjectMocks
    private AssetsConverter assetsConverter;

    @Mock
    private BpnRepository bpnRepository;

    @Mock
    private TraceabilityProperties traceabilityProperties;

    @Test
    void testAssetConverterAddsParentAssets() throws IOException {
        // Given
         ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream file = AssetsConverter.class.getResourceAsStream("/data/irs_assets_v2_singleUsageAsBuilt.json");
        JobResponse response = mapper.readValue(file, JobResponse.class);
        when(traceabilityProperties.getBpn()).thenReturn()
        // when
        List<Asset> assets = assetsConverter.convertAssets(response);
        Asset ownAsset = assets.get(0);
        Asset parentAsset = assets.get(1);

        // then
        final String ownAssetId = "urn:uuid:8f9d8c7f-6d7a-48f1-9959-9fa3a1a7a891";
        final String parentAssetId = "urn:uuid:3e300930-0e1c-459c-8914-1ac631176716";
        assertThat(ownAsset.getId()).isEqualTo(ownAssetId);
        assertThat(parentAsset.getId()).isEqualTo(parentAssetId);
        assertThat(ownAsset.getParentDescriptions().get(0).id()).isEqualTo(parentAssetId);}

}
