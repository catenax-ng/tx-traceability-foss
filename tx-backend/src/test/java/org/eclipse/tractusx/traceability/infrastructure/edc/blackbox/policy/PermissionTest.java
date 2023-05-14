package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

    @Test
    void builder() {
        // given
        final Duty duty1 = Duty.Builder.newInstance().build();

        // when
        final Permission result = Permission.Builder
                .newInstance()
                .duty(duty1)
                .uid("uid")
                .build();

        // then
        assertThat(result.getDuties()).hasSize(1);
    }

    @Test
    void builderDuty() {
        // given
        final Permission permission = Permission.Builder
                .newInstance().build();
        final Duty duty1 = Duty.Builder.newInstance().build();

        final Duty duty2 = Duty.Builder.newInstance()
                .parentPermission(permission)
                .consequence(duty1)
                .build();

        // when
        final Permission result = Permission.Builder
                .newInstance()
                .duties(List.of(duty1, duty2))
                .uid("uid")
                .build();

        // then
        assertThat(result.getDuties()).hasSize(2);
    }

    @Test
    void toStringMethod() {
        // given
        final Permission permission = Permission.Builder
                .newInstance()
                .uid("uid")
                .build();

        // when
        final String result = permission.toString();

        // then
        assertThat(result).isEqualTo("Permission constraints: []");
    }

}
