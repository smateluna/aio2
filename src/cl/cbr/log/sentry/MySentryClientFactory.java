package cl.cbr.log.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.dsn.Dsn;
import io.sentry.event.helper.ContextBuilderHelper;
import io.sentry.event.helper.HttpEventBuilderHelper;

/**
 * Proyecto: aio
 * Creado por: jaguileram
 * Fecha: 22-09-2017
 */
public class MySentryClientFactory extends DefaultSentryClientFactory {
    @Override
    public SentryClient createSentryClient(Dsn dsn) {
        SentryClient sentryClient = new SentryClient(createConnection(dsn), getContextManager(dsn));

        MyForwardedAddressResolver myForwardedAddressResolver = new MyForwardedAddressResolver();
        sentryClient.addBuilderHelper(new HttpEventBuilderHelper(myForwardedAddressResolver));

        sentryClient.addBuilderHelper(new ContextBuilderHelper(sentryClient));
        return configureSentryClient(sentryClient, dsn);
    }

}
