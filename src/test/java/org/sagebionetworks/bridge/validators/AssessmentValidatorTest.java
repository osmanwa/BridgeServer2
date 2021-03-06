package org.sagebionetworks.bridge.validators;

import static org.sagebionetworks.bridge.BridgeConstants.API_STUDY_ID_STRING;
import static org.sagebionetworks.bridge.BridgeConstants.BRIDGE_EVENT_ID_ERROR;
import static org.sagebionetworks.bridge.TestConstants.IDENTIFIER;
import static org.sagebionetworks.bridge.TestConstants.OWNER_ID;
import static org.sagebionetworks.bridge.TestConstants.SHARED_STUDY_IDENTIFIER;
import static org.sagebionetworks.bridge.TestConstants.TEST_STUDY;
import static org.sagebionetworks.bridge.TestUtils.assertValidatorMessage;
import static org.sagebionetworks.bridge.validators.AssessmentValidator.CANNOT_BE_BLANK;

import com.google.common.collect.ImmutableList;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.sagebionetworks.bridge.dao.AssessmentDao;
import org.sagebionetworks.bridge.models.PagedResourceList;
import org.sagebionetworks.bridge.models.assessments.Assessment;
import org.sagebionetworks.bridge.models.assessments.AssessmentTest;
import org.sagebionetworks.bridge.models.substudies.Substudy;
import org.sagebionetworks.bridge.services.SubstudyService;

public class AssessmentValidatorTest extends Mockito {

    @Mock
    AssessmentDao mockAssessmentDao;
    
    @Mock
    SubstudyService mockSubstudyService;
    
    AssessmentValidator validator;
    
    Assessment assessment;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        assessment = AssessmentTest.createAssessment();
        
        when(mockAssessmentDao.getAssessmentRevisions(API_STUDY_ID_STRING, IDENTIFIER, 0, 1, true))
            .thenReturn(new PagedResourceList<Assessment>(ImmutableList.of(), 0));
        
        validator = new AssessmentValidator(mockSubstudyService, API_STUDY_ID_STRING);
    }
    
    @Test
    public void validAssessment() {
        when(mockSubstudyService.getSubstudy(TEST_STUDY, assessment.getOwnerId(), false))
            .thenReturn(Substudy.create());
        
        Validate.entityThrowingException(validator, assessment);
    }
    @Test
    public void validSharedAssessment() {
        validator = new AssessmentValidator(mockSubstudyService, SHARED_STUDY_IDENTIFIER);
        assessment.setOwnerId(API_STUDY_ID_STRING + ":" + OWNER_ID);
        
        when(mockSubstudyService.getSubstudy(TEST_STUDY, OWNER_ID, false)).thenReturn(Substudy.create());
    
        Validate.entityThrowingException(validator, assessment);
    }
    @Test
    public void ownerIdInvalid() {
        assertValidatorMessage(validator, assessment, "ownerId", "is not a valid organization ID");
    }
    @Test
    public void guidNull() {
        assessment.setGuid(null);
        assertValidatorMessage(validator, assessment, "guid", CANNOT_BE_BLANK);
    }
    @Test
    public void guidEmpty() {
        assessment.setGuid("  ");
        assertValidatorMessage(validator, assessment, "guid", CANNOT_BE_BLANK);
    }
    @Test
    public void titleNull() {
        assessment.setTitle(null);
        assertValidatorMessage(validator, assessment, "title", CANNOT_BE_BLANK);
    }
    @Test
    public void titleEmpty() {
        assessment.setTitle("");
        assertValidatorMessage(validator, assessment, "title", CANNOT_BE_BLANK);
    }
    @Test
    public void osNameNull() {
        assessment.setOsName(null);
        assertValidatorMessage(validator, assessment, "osName", CANNOT_BE_BLANK);
    }
    @Test
    public void osNameEmpty() {
        assessment.setOsName("\n");
        assertValidatorMessage(validator, assessment, "osName", CANNOT_BE_BLANK);
    }
    @Test
    public void osNameInvalid() {
        assessment.setOsName("webOS");
        assertValidatorMessage(validator, assessment, "osName", "is not a supported platform");
    }
    @Test
    public void identifierNull() {
        assessment.setIdentifier(null);
        assertValidatorMessage(validator, assessment, "identifier", CANNOT_BE_BLANK);
    }
    @Test
    public void identifierEmpty() {
        assessment.setIdentifier("   ");
        assertValidatorMessage(validator, assessment, "identifier", CANNOT_BE_BLANK);
    }
    @Test
    public void identifierInvalid() {
        assessment.setIdentifier("spaces are not allowed");
        assertValidatorMessage(validator, assessment, "identifier", BRIDGE_EVENT_ID_ERROR);
    }
    @Test
    public void revisionNegative() {
        assessment.setRevision(-3);
        assertValidatorMessage(validator, assessment, "revision", "cannot be negative");
    }
    @Test
    public void ownerIdNull() {
        assessment.setOwnerId(null);
        assertValidatorMessage(validator, assessment, "ownerId", CANNOT_BE_BLANK);
    }
    @Test
    public void ownerIdEmpty() {
        assessment.setOwnerId("\t");
        assertValidatorMessage(validator, assessment, "ownerId", CANNOT_BE_BLANK);
    }
}
