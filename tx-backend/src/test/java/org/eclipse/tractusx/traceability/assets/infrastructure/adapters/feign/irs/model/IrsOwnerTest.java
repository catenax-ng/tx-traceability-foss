package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model;

import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IrsOwnerTest {

    @ParameterizedTest
    @MethodSource("provideValues")
    void givenIrsOwner_WhenToDomain_ThenConvertCorrectly(
            IrsOwner input,
            Owner expectedOutput) {
        // given input

        // when
        Owner result = input.toDomain();

        // then
        assertThat(result).isEqualTo(expectedOutput);
    }

    private static Stream<Arguments> provideValues() {
        return Stream.of(
                Arguments.of(IrsOwner.OWN, Owner.OWN),
                Arguments.of(IrsOwner.SUPPLIER, Owner.SUPPLIER),
                Arguments.of(IrsOwner.CUSTOMER, Owner.CUSTOMER)
        );
    }
}
