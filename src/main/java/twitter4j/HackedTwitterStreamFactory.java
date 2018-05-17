package twitter4j;

import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

public class HackedTwitterStreamFactory {

    private static final long serialVersionUID = -5181136070759074681L;
    private final Configuration conf;
    private final String consumerKey;
    private static final TwitterStream SINGLETON;

    static {
        SINGLETON = new TwitterStreamImpl(ConfigurationContext.getInstance(), TwitterFactory.DEFAULT_AUTHORIZATION);
    }

    /**
     * Creates a TwitterStreamFactory with the root configuration.
     */
    public HackedTwitterStreamFactory(String theConsumerKey) {
        this(theConsumerKey, ConfigurationContext.getInstance());
    }

    /**
     * Creates a TwitterStreamFactory with the given configuration.
     *
     * @param conf the configuration to use
     * @since Twitter4J 2.1.1
     */
    public HackedTwitterStreamFactory(String theConsumerKey, Configuration conf) {
        this.conf = conf;
        this.consumerKey = theConsumerKey;
    }

    // viewImplementations for BasicSupportFactory

    /**
     * Returns a instance associated with the configuration bound to this factory.
     *
     * @return default instance
     */
    public TwitterStream getInstance() {
        return getInstance(AuthorizationFactory.getInstance(conf));
    }

    /**
     * Returns a OAuth Authenticated instance.<br>
     * consumer key and consumer Secret must be provided by twitter4j.properties, or system properties.
     * Unlike {@link TwitterStream#setOAuthAccessToken(AccessToken)}, this factory method potentially returns a cached instance.
     *
     * @param accessToken access token
     * @return an instance
     */
    public TwitterStream getInstance(AccessToken accessToken) {
        String consumerKey = conf.getOAuthConsumerKey();
        String consumerSecret = conf.getOAuthConsumerSecret();
        if (null == consumerKey && null == consumerSecret) {
            throw new IllegalStateException("Consumer key and Consumer secret not supplied.");
        }
        OAuthAuthorization oauth = new OAuthAuthorization(conf);
        oauth.setOAuthAccessToken(accessToken);
        return getInstance(conf, oauth);
    }

    /**
     * Returns a instance.
     *
     * @param auth authorization object to be associated
     * @return an instance
     */
    public TwitterStream getInstance(Authorization auth) {
        return getInstance(conf, auth);
    }

    private TwitterStream getInstance(Configuration conf, Authorization auth) {
        return new HackedTwitterStreamImpl(this.consumerKey, conf, auth);
    }

    /**
     * Returns default singleton TwitterStream instance.
     *
     * @return default singleton TwitterStream instance
     * @since Twitter4J 2.2.4
     */
    public static TwitterStream getSingleton() {
        return SINGLETON;
    }
}
