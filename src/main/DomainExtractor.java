package com.eulerity.hackathon.imagefinder;

import java.net.URI;
import java.net.URISyntaxException;

public class DomainExtractor {
    /**
     * Returns the domain of an url
     * @param urlString an absolute URL
     * @return          the scheme plus domains of the url
     */
    public static String extractDomain(String urlString) {
        try {
            URI url = new URI(urlString);
            String scheme = url.getScheme();
            String domain = url.getHost();
            if (domain != null) {
                return (scheme != null ? scheme + "://" : "") + (domain.startsWith("www.") ? domain.substring(4) : domain);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}