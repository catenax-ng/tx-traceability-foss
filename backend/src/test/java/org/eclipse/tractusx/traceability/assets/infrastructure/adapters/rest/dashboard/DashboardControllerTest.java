package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.rest.dashboard;

import org.eclipse.tractusx.traceability.assets.domain.model.Dashboard;
import org.eclipse.tractusx.traceability.assets.domain.service.DashboardService;
import org.eclipse.tractusx.traceability.common.security.JwtAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    DashboardService dashboardService;

    @Test
    void dashboard() {
        JwtAuthentication jwtAuthentication = null;
        Dashboard dashboard = new Dashboard(9L, 99L, 999L);
        Mockito.when(dashboardService.getDashboard(jwtAuthentication)).thenReturn(dashboard);
        Dashboard testDashboard = dashboardService.getDashboard(jwtAuthentication);

        assertEquals(9, testDashboard.myItems());
        assertEquals(99, testDashboard.otherParts());
        assertEquals(999, testDashboard.investigations());
    }

}
