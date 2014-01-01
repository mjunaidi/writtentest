package example.plugins.writtentest;

import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.HashMap;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.polopoly.cm.app.policy.DateTimePolicy;
import com.polopoly.cm.app.policy.SingleValuePolicy;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;

public class PremiumArticleTest extends TestCase {
    private PremiumArticlePolicy target;

    @Mock HashMap<String, Policy> children;
    @Mock Content content;
    @Mock InputTemplate inputTemplate;
    @Mock Policy parent;
    @Mock PolicyCMServer cmServer;
    @Mock SingleValuePolicy premiumPolicy;
    @Mock DateTimePolicy pubdatePolicy;

    protected void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        target = new PremiumArticlePolicy() {
            @Override
            protected void initChildPolicies() {
                this.childPolicies = children;
            }
        };
        target.init("PolicyName", new Content[] { content }, inputTemplate, parent, cmServer);
    }
    
    public void testArticleIsPremiumArticle() throws Exception {
        when(children.get("premium")).thenReturn(premiumPolicy);
        when(premiumPolicy.getValue()).thenReturn("true");
        assertTrue(target.isPremiumArticle());
        
    }
    
    public void testArticleIsNotPremiumArticle() throws Exception {
        when(children.get("premium")).thenReturn(premiumPolicy);
        when(premiumPolicy.getValue()).thenReturn("false");
        assertFalse(target.isPremiumArticle());
    }
    
    public void testUserIsLoggedIn() throws Exception {
        when(cmServer.getCurrentCaller()).thenReturn(new Caller(new UserId("98")));
        assertTrue(target.isUserLoggedIn());
    }
    
    public void testUserIsNotLoggedIn() throws Exception {
        when(cmServer.getCurrentCaller()).thenReturn(Caller.NOBODY_CALLER);
        assertFalse(target.isUserLoggedIn());
    }
    
    public void testPubdateIsLessThan30Days() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        
        when(children.get("publishedDate")).thenReturn(pubdatePolicy);
        when(pubdatePolicy.getDate()).thenReturn(cal.getTime());
        assertTrue(target.isPubdateLessThan30Days());
    }
    
    public void testPubdateIsMoreThan30Days() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -40);
        
        when(children.get("publishedDate")).thenReturn(pubdatePolicy);
        when(pubdatePolicy.getDate()).thenReturn(cal.getTime());
        assertFalse(target.isPubdateLessThan30Days());
    }
}
