package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.CONFIRMED;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.CONFIRMING;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.CONSUMER_APPROVED;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.CONSUMER_APPROVING;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.CONSUMER_OFFERED;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.CONSUMER_OFFERING;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.DECLINED;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.DECLINING;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.ERROR;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.INITIAL;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.PROVIDER_OFFERED;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.PROVIDER_OFFERING;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.REQUESTED;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.REQUESTING;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.UNSAVED;

class ContractNegotiationStatesTest {

    private static int NON_EXISTENT_CODE = 69;

    @ParameterizedTest
    @MethodSource("provideContractNegotiationStates")
    void givenContractNegotiationState_whenFrom_thenReturnCorrectState(
            ContractNegotiationStates expectedResult,
            Integer input
    ) {
        // when
        final ContractNegotiationStates result = ContractNegotiationStates.from(input);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideContractNegotiationStates() {
        return Stream.of(
                Arguments.of(UNSAVED, 0),
                Arguments.of(INITIAL, 50),
                Arguments.of(REQUESTING, 100),
                Arguments.of(REQUESTED, 200),
                Arguments.of(PROVIDER_OFFERING, 300),
                Arguments.of(PROVIDER_OFFERED, 400),
                Arguments.of(CONSUMER_OFFERING, 500),
                Arguments.of(CONSUMER_OFFERED, 600),
                Arguments.of(CONSUMER_APPROVING, 700),
                Arguments.of(CONSUMER_APPROVED, 800),
                Arguments.of(DECLINING, 900),
                Arguments.of(DECLINED, 1000),
                Arguments.of(CONFIRMING, 1100),
                Arguments.of(CONFIRMED, 1200),
                Arguments.of(ERROR, -1),
                Arguments.of(null, NON_EXISTENT_CODE)

        );
    }

    ;

}
