package example.plugins.writtentest;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.model.Model;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

public class PremiumArticleController extends RenderControllerBase {
    protected static String CLASS = PremiumArticleController.class.getName();
    protected static Logger logger = Logger.getLogger(CLASS);
    
    @Override
    public void populateModelBeforeCacheKey(RenderRequest request, TopModel m, ControllerContext context) {
        super.populateModelBeforeCacheKey(request, m, context);
        
        Model contentModel = context.getContentModel();
        try {
            PremiumArticlePolicy policy = (PremiumArticlePolicy) contentModel.getAttribute("_data");

            boolean articleAccess = true;
            if (policy.isPremiumArticle() && !policy.isUserLoggedIn() && policy.isPubdateLessThan30Days()) {
                articleAccess = false;
            }

            ModelPathUtil.set(m.getLocal(), "articleAccess", articleAccess);

        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
