package cl.cbr.log.sentry;

import io.sentry.event.helper.BasicRemoteAddressResolver;
import io.sentry.event.helper.RemoteAddressResolver;
import io.sentry.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Proyecto: aio
 * Creado por: jaguileram
 * Fecha: 26-12-2017
 */
public class MyForwardedAddressResolver implements RemoteAddressResolver {
    private BasicRemoteAddressResolver basicRemoteAddressResolver = new BasicRemoteAddressResolver();

    private static String firstAddress(String csvAddrs) {
    	System.out.println();
        List<String> ips = Arrays.asList(csvAddrs.split(","));
        return ips.get(0).trim();
    }

    public String getRemoteAddress(HttpServletRequest request) {
    	System.out.println("RESOLVIENDO REQUEST"+request);
    	  String forwarded = request.getHeader("X-REAL-IP");
        return !Util.isNullOrEmpty(forwarded) ? firstAddress(forwarded) : this.basicRemoteAddressResolver.getRemoteAddress(request);
    }
}