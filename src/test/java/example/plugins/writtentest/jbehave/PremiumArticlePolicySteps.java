package example.plugins.writtentest.jbehave;

import static org.jbehave.Ensure.ensureThat;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.HashMap;

import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.polopoly.cm.app.policy.DateTimePolicy;
import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;

import example.plugins.writtentest.PremiumArticlePolicy;

public class PremiumArticlePolicySteps extends Steps {
    
    private PremiumArticlePolicy target;
    
    @Mock HashMap<String, Policy> children;
    @Mock Content content;
    @Mock InputTemplate inputTemplate;
    @Mock Policy parent;
    @Mock PolicyCMServer cmServer;

    @Mock SingleValuePolicy premiumPolicy;
    @Mock DateTimePolicy pubdatePolicy;
    
    public PremiumArticlePolicySteps() {
        MockitoAnnotations.initMocks(this);
        
        target = new PremiumArticlePolicy() {
            @Override
            protected void initChildPolicies()
            {
                this.childPolicies = children;
            }
        };
        target.init("PolicyName", new Content[] { content },
                inputTemplate, parent, cmServer);

        
        when(children.get("premium")).thenReturn(premiumPolicy);
    }
    
    @Given("a $premium article")
    public void initArticleAndViewDate(String premium) throws CMException {
        when(premiumPolicy.getValue()).thenReturn( ((Boolean)"premium".equals(premium)).toString());
    }
    
    @When("pubdate is $days days from now")
    public void initPubDate(int days) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -days);
        
        when(children.get("publishedDate")).thenReturn(pubdatePolicy);
        when(pubdatePolicy.getDate()).thenReturn(cal.getTime());
    }
    
    @When("user is $user")
    public void initUser(String user) {
        Caller caller = Caller.NOBODY_CALLER;
        if (!"anonymous_user".equals(user)) {
            caller = new Caller(new UserId("98"), "");
        }
        
        when(cmServer.getCurrentCaller()).thenReturn(caller);
    }
    
    @Then("access is $accessStr")
    public void verifyArticleAccess(String accessStr) throws CMException {
        Boolean articleAccess = Boolean.valueOf(accessStr);
        
        boolean access = true;
        if (target.isPremiumArticle()) {
            if (target.isPubdateLessThan30Days() && !target.isUserLoggedIn()) {
                access = false;
            }
        }
        ensureThat(articleAccess == access);
    }
    
}
