package cl.cbr.log.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.dsn.Dsn;
import io.sentry.event.helper.ContextBuilderHelper;
import io.sentry.event.helper.ForwardedAddressResolver;
import io.sentry.event.helper.HttpEventBuilderHelper;

/**
 * Proyecto: ws-escucha
 * Creado por: jaguileram
 * Fecha: 22-09-2017
 */
public class MySentryClientFactory extends DefaultSentryClientFactory {
    @Override
    public SentryClient createSentryClient(Dsn dsn) {
    	System.out.println("DSN---->"+dsn);
        SentryClient sentryClient = new SentryClient(createConnection(dsn), getContextManager(dsn));
        /*
        Create and use the ForwardedAddressResolver, which will use the
        X-FORWARDED-FOR header for the remote address if it exists.
         */

        //sentryClient.addBuilderHelper(new MyHttpEventBuilderHelper());

        ForwardedAddressResolver forwardedAddressResolver = new ForwardedAddressResolver();
        sentryClient.addBuilderHelper(new HttpEventBuilderHelper(forwardedAddressResolver));

        sentryClient.addBuilderHelper(new ContextBuilderHelper(sentryClient));
        return configureSentryClient(sentryClient, dsn);
    }

}
