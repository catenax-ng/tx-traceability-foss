package org.eclipse.tractusx.traceability.assets.application.rest.response;

import org.eclipse.tractusx.traceability.assets.domain.model.Owner;

public enum OwnerResponse {
    SUPPLIER, CUSTOMER, OWN;

    public static OwnerResponse from(final Owner owner) {
        return OwnerResponse.valueOf(owner.name());
    }
}
