package TTSW.Postify;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AuthenticationUnitTest.class,
        AuthenticationIntegrationTest.class,
})
public class AllTestsSuite {
    // yes, this should be empty
}