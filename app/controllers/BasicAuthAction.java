package controllers;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.commons.codec.binary.Base64;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class BasicAuthAction extends Action<Result> {
    private static ALogger log = Logger.of(BasicAuthAction.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String WWW_AUTHENTICATE = "Authorization";
    private static final String FAIL = "Authorization failed";

    @Override
    public CompletionStage<Result> call(Context context) {
        Optional<String> authHeader = context.request().header(AUTHORIZATION);
        if (authHeader == null) {
            context.response().setHeader(WWW_AUTHENTICATE, FAIL);
            return CompletableFuture.completedFuture(status(Http.Status.UNAUTHORIZED, "Needs authorization"));
        }

        String[] credentials;
        try {
            credentials = parseAuthHeader(authHeader.get());
        } catch (Exception e) {
            log.warn("Cannot parse basic auth info", e);
            return CompletableFuture.completedFuture(status(Http.Status.FORBIDDEN, "Invalid auth header"));
        }

        String username = credentials[0];
        String password = credentials[1];
        boolean loginCorrect = checkLogin(username, password);

        if (!loginCorrect) {
            log.warn("Incorrect basic auth login, username=" + username);
            return CompletableFuture.completedFuture(status(Http.Status.FORBIDDEN, "Incorrect basic auth login, username= " + username));
        } else {
            context.request().withAttrs(context.request().attrs().put(Security.USERNAME, username));
            log.info("Successful basic auth login, username=" + username);
            return delegate.call(context);
        }
    }

    private String[] parseAuthHeader(String authHeader) throws UnsupportedEncodingException {
        if (!authHeader.startsWith("Basic ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String[] credString;
        String auth = authHeader.substring(6);
        byte[] decodedAuth = new Base64().decode(auth);
        credString = new String(decodedAuth, "UTF-8").split(":", 2);
        if (credString.length != 2) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        return credString;
    }

    private boolean checkLogin(String username, String password) {
        /// change this
        return username.equals("Administrator") && password.equals("password") ;
    }
}