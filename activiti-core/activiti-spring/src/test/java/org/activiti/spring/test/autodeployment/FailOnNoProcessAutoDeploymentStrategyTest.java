package org.activiti.spring.test.autodeployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.test.TestHelper;
import org.activiti.spring.autodeployment.FailOnNoProcessAutoDeploymentStrategy;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:org/activiti/spring/test/autodeployment/errorHandling/spring-context.xml")
public class FailOnNoProcessAutoDeploymentStrategyTest extends SpringActivitiTestCase {

    private final String nameHint = "FailOnNoProcessAutoDeploymentStrategyTest";

    private final String validName1 = "org/activiti/spring/test/autodeployment/errorHandling/valid.bpmn20.xml";
    private final String invalidName1 = "org/activiti/spring/test/autodeployment/errorHandling/parsing-error.bpmn20.xml";
    private final String invalidName2 = "org/activiti/spring/test/autodeployment/errorHandling/validation-error.bpmn20.xml";

    @Before
    public void setUp() {
        TestHelper.cleanUpDeployments(repositoryService);
    }

    @Test
    public void testValidResources() {
        final Resource[] resources = new Resource[] {new ClassPathResource(validName1)};
        FailOnNoProcessAutoDeploymentStrategy deploymentStrategy = new FailOnNoProcessAutoDeploymentStrategy(null);
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertThat(repositoryService.createDeploymentQuery().count()).isEqualTo(1);
    }

    @Test
    public void testInvalidResources() {
        final Resource[] resources = new Resource[] {new ClassPathResource(validName1), new ClassPathResource(invalidName1), new ClassPathResource(invalidName2)};
        FailOnNoProcessAutoDeploymentStrategy deploymentStrategy = new FailOnNoProcessAutoDeploymentStrategy(null);
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertThat(repositoryService.createDeploymentQuery().count()).isEqualTo(1);
    }

    @Test
    public void testWithParsingErrorResources() {
        final Resource[] resources = new Resource[] {new ClassPathResource(validName1), new ClassPathResource(invalidName1)};
        FailOnNoProcessAutoDeploymentStrategy deploymentStrategy = new FailOnNoProcessAutoDeploymentStrategy(null);
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertThat(repositoryService.createDeploymentQuery().count()).isEqualTo(1);
    }

    @Test
    public void testWithValidationErrorResources() {
        final Resource[] resources = new Resource[] {new ClassPathResource(validName1), new ClassPathResource(invalidName2)};
        FailOnNoProcessAutoDeploymentStrategy deploymentStrategy = new FailOnNoProcessAutoDeploymentStrategy(null);
        deploymentStrategy.deployResources(nameHint, resources, repositoryService);
        assertThat(repositoryService.createDeploymentQuery().count()).isEqualTo(1);
    }

    @Test
    public void testOnlyInvalidResources() {
        final Resource[] resources = new Resource[] {new ClassPathResource(invalidName1)};
        FailOnNoProcessAutoDeploymentStrategy deploymentStrategy = new FailOnNoProcessAutoDeploymentStrategy(null);
        assertThat(repositoryService.createDeploymentQuery().count()).isEqualTo(0);
        assertThatExceptionOfType(ActivitiException.class)
            .isThrownBy(() -> deploymentStrategy.deployResources(nameHint, resources, repositoryService))
            .withMessage("No process definition was deployed.");
        assertThat(repositoryService.createDeploymentQuery().count()).isEqualTo(0);
    }
}
