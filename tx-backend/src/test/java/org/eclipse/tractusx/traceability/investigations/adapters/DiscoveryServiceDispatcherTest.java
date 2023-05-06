package org.eclipse.tractusx.traceability.investigations.adapters;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscoveryServiceDispatcherTest {

   /* @InjectMocks
    private DiscoveryServiceDispatcher edcUrlProviderDispatcher;

    @Mock
    private FeignDiscoveryRepository feignDiscoveryRepository;

    @Mock
    private EdcProperties edcProperties;

    @Mock
    private BpnEdcMappingRepository bpnEdcMappingRepository;

    private static final String FALLBACK_URL = "https://trace-x-test-edc.test.demo.catena-x.net";

    @BeforeEach
    void setup() {
        edcUrlProviderDispatcher = new DiscoveryServiceDispatcher(feignDiscoveryRepository, edcProperties, bpnEdcMappingRepository);
    }

    @Test
    void testEdcUrlProviderDispatcherGetSenderUrl() {
        // given
        String senderUrl = "https://some-edc-url.com";

        // and
        when(edcProperties.getProviderEdcUrl()).thenReturn(senderUrl);

        // when
        String result = edcUrlProviderDispatcher.getSenderUrl();

        // then
        assertThat(result).isEqualTo(senderUrl);
    }

    @Test
    void testEdcUrlProviderDispatcherGetEdcUrlsFromPortalAndFallback() {
        // given
        String bpn = "BPN1234";
        String connectorEndpoint = "https://some-edc-url.com";
        BpnEdcMapping bpnEdcMapping = new BpnEdcMapping(bpn, FALLBACK_URL);
        // and
        when(feignDiscoveryRepository.getConnectorEndpointMappings(List.of(bpn)))
                .thenReturn(List.of(new ConnectorDiscoveryMappingResponse(bpn, List.of(connectorEndpoint))));
        when(bpnEdcMappingRepository.exists(bpn)).thenReturn(true);
        when(bpnEdcMappingRepository.findById(bpn)).thenReturn(bpnEdcMapping);
        // when
        List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

        // then
        List<String> expectedEdcUrls = new ArrayList<>(List.of(connectorEndpoint));
        expectedEdcUrls.add(FALLBACK_URL);
        assertThat(edcUrls).isEqualTo(expectedEdcUrls).hasSize(2);
    }

    @Test
    void testEdcUrlProviderDispatcherGetEdcUrlsFromFallbackMappingOnServiceUnavailable() {
        // given
        String bpn = "BPN1234";
        BpnEdcMapping bpnEdcMapping = new BpnEdcMapping(bpn, FALLBACK_URL);
        when(feignDiscoveryRepository.getConnectorEndpointMappings(List.of(bpn)))
                .thenThrow(serviceUnavailable());
        when(bpnEdcMappingRepository.exists(bpn)).thenReturn(true);
        when(bpnEdcMappingRepository.findById(bpn)).thenReturn(bpnEdcMapping);
        // when
        List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

        // then
        assertThat(edcUrls).isEqualTo(List.of(FALLBACK_URL));
    }

    @Test
    void testEdcUrlProviderDispatcherGetEdcUrlsFromFallbackMappingOnNullResponse() {
        // given
        String bpn = "BPN1234";
        BpnEdcMapping bpnEdcMapping = new BpnEdcMapping(bpn, FALLBACK_URL);
        // and
        when(feignDiscoveryRepository.getConnectorEndpointMappings(List.of(bpn))).thenReturn(null);
        when(bpnEdcMappingRepository.exists(bpn)).thenReturn(true);
        when(bpnEdcMappingRepository.findById(bpn)).thenReturn(bpnEdcMapping);
        // when
        List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

        // then
        assertThat(edcUrls).isEqualTo(List.of(FALLBACK_URL));
    }

    @Test
    void testEdcUrlProviderDispatcherGetEdcUrlsFromFallbackDefaultMapping() {
        // given
        String bpn = "BPN1234";
        BpnEdcMapping bpnEdcMapping = new BpnEdcMapping(bpn, FALLBACK_URL);
        // and
        when(feignDiscoveryRepository.getConnectorEndpointMappings(List.of(bpn)))
                .thenThrow(new RuntimeException("unit-tests"));

        when(bpnEdcMappingRepository.exists(bpn)).thenReturn(true);
        when(bpnEdcMappingRepository.findById(bpn)).thenReturn(bpnEdcMapping);
        // when
        List<String> edcUrls = edcUrlProviderDispatcher.getEdcUrls(bpn);

        // then
        assertThat(edcUrls).isEqualTo(List.of(FALLBACK_URL));
    }

    private FeignException.ServiceUnavailable serviceUnavailable() {
        return new FeignException.ServiceUnavailable(
                "unit-tests",
                Request.create(Request.HttpMethod.POST, "", Map.of(), new byte[]{}, null, null),
                null, null);
    }*/
}
