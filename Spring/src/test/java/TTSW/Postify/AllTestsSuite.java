package TTSW.Postify;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AuthenticationUnitTest.class,
        AuthenticationIntegrationTest.class,
        PostUnitTest.class,
        PostIntegrationTest.class,
        MediumUnitTest.class,
        MediumIntegrationTest.class,
        CommentUnitTest.class,
        CommentIntegrationTest.class,
        AuthorizationUnitTest.class,
        AuthorizationIntegrationTest.class,
})
public class AllTestsSuite {
    // yes, this should be empty
}