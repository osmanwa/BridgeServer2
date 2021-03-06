package org.sagebionetworks.bridge.spring.controllers;

import static org.sagebionetworks.bridge.Roles.DEVELOPER;
import static org.sagebionetworks.bridge.TestUtils.assertAccept;
import static org.testng.Assert.assertEquals;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import org.sagebionetworks.bridge.TestConstants;
import org.sagebionetworks.bridge.models.StatusMessage;
import org.sagebionetworks.bridge.models.accounts.UserSession;
import org.sagebionetworks.bridge.services.ExportService;

public class ExportControllerTest extends Mockito {
    @Test
    public void test() throws Exception {
        assertAccept(ExportController.class, "startOnDemandExport");
        
        // spy controller
        ExportController controller = spy(new ExportController());

        // mock session
        UserSession mockSession = new UserSession();
        mockSession.setStudyIdentifier(TestConstants.TEST_STUDY);
        doReturn(mockSession).when(controller).getAuthenticatedSession(any());

        // mock service
        ExportService mockExportService = mock(ExportService.class);
        controller.setExportService(mockExportService);

        // execute and validate
        StatusMessage result = controller.startOnDemandExport();
        assertEquals(result.getMessage(), "Request submitted.");
        verify(mockExportService).startOnDemandExport(TestConstants.TEST_STUDY);
        verify(controller).getAuthenticatedSession(DEVELOPER);
    }
}
