package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model;

import org.eclipse.tractusx.traceability.assets.domain.model.Owner;

public enum IrsOwner {
    SUPPLIER, CUSTOMER, OWN;

    Owner toDomain() {
        return Owner.valueOf(this.name());
    }
}
