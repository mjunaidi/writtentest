package example.plugins.writtentest.jbehave;

import org.jbehave.scenario.JUnitScenario;

public class PremiumArticlePolicyScenario extends JUnitScenario {
    public PremiumArticlePolicyScenario() {
        super(new PremiumArticlePolicySteps());
    }
}
