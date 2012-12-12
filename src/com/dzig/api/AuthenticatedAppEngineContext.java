package com.dzig.api;


import android.accounts.Account;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import com.dzig.utils.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public final class AuthenticatedAppEngineContext implements HttpContext {
    private HttpContext delegate_;
    private CookieStore cookieStore_;

    public static HttpContext newInstance(Context context, String uri, String token) {
        if (context == null)
            throw new IllegalArgumentException("context is null");
        return new AuthenticatedAppEngineContext(context, uri, token);
    }

    private AuthenticatedAppEngineContext(Context context, String uri, String authToken){
        delegate_ = new BasicHttpContext();
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance(
                "GetAuthCookieClient", context);
        try {
            httpClient.getParams().setBooleanParameter(
                    ClientPNames.HANDLE_REDIRECTS, false);
            cookieStore_ = new BasicCookieStore();
            setAttribute(ClientContext.COOKIE_STORE, cookieStore_);
            HttpGet http_get = new HttpGet(uri
                    + "/_ah/login?continue=http://localhost/&auth=" + authToken);
            HttpResponse response = httpClient.execute(http_get, this);
            checkResponse(cookieStore_, response);
        } catch (IOException e) {
            Logger.error("ApiClient", "Unable to authenticate", e);
        } finally {
            httpClient.close();
        }

    }

    private void checkResponse(CookieStore cookieStore, HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != 302) {
            Logger.error("ApiClient",
                    "authentication response was not a redirect");
        }
        if (!isAuthenticationCookiePresent(cookieStore)) {
            Logger.error("ApiClient",
                    "authentication cookie not found in cookie store");
        }
    }

    private boolean isAuthenticationCookiePresent(CookieStore cookieStore) {
        for (Cookie cookie : cookieStore.getCookies()) {
            if (cookie.getName().equals("ACSID")
                    || cookie.getName().equals("SACSID"))
                return true;
        }
        return false;
    }

    public Object getAttribute(String id) {
        return delegate_.getAttribute(id);
    }

    public Object removeAttribute(String id) {
        return delegate_.removeAttribute(id);
    }

    public void setAttribute(String id, Object obj) {
        delegate_.setAttribute(id, obj);
    }

}