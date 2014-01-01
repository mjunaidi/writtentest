package example.plugins.writtentest;

import java.util.Date;

import com.polopoly.cm.app.policy.DateTimePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.user.server.Caller;

import example.content.article.StandardArticlePolicy;

public class PremiumArticlePolicy extends StandardArticlePolicy {

    public boolean isPremiumArticle() {
        return Boolean.valueOf(getChildValue("premium"));
    }

    public boolean isUserLoggedIn() {
        
        Caller caller = getCMServer().getCurrentCaller();
        return !Caller.NOBODY_CALLER.equals(caller);
    }

    public boolean isPubdateLessThan30Days() throws CMException {
        DateTimePolicy pubdatePolicy = (DateTimePolicy) getChildPolicy("publishedDate");
        Date pubdate = pubdatePolicy.getDate();
        
        Date today = new Date();
        return today.getTime() - pubdate.getTime() <= 30 * 24 * 60 * 60 * 1000l;
    }

}
