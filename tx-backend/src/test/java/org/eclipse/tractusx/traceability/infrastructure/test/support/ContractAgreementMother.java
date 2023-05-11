package org.eclipse.tractusx.traceability.infrastructure.test.support;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.agreement.ContractAgreement;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;

public class ContractAgreementMother {
    public static ContractAgreement getContractAgreement(final Policy policy) {
        return ContractAgreement.Builder
                .newInstance()
                .id("id")
                .providerAgentId("providerAgentId")
                .consumerAgentId("consumerAgentId")
                .contractSigningDate(1)
                .contractStartDate(2)
                .contractEndDate(3)
                .assetId("assetId")
                .policy(policy)
                .build();
    }

    public static Policy getPolicy() {
        return Policy.Builder.newInstance()
                .build();
    }
}
