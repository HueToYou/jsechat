package org.huetoyou.jsechat.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jodd.jerry.Jerry;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static jodd.jerry.Jerry.jerry;

/**
 * Performs each step of the authentication process
 */
class AuthenticatorRunnable implements Runnable {

    interface Listener {
        void onFailed(String message);
        void onSucceeded(List<Cookie> cookies);
    }

    private class SimpleCookieJar implements CookieJar {

        private final List<Cookie> mCookies = new ArrayList<>();

        List<Cookie> cookies() {
            return mCookies;
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            mCookies.addAll(cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            return mCookies;
        }
    }

    private SimpleCookieJar mCookieJar = new SimpleCookieJar();
    private OkHttpClient mClient = new OkHttpClient.Builder()
            .cookieJar(mCookieJar)
            .build();

    private String mEmail;
    private String mPassword;
    private Listener mListener;

    private String mLoginUrl;
    private String mNetworkFkey;
    private String mAuthUrl;
    private String mSessionId;
    private String mSessionFkey;

    public AuthenticatorRunnable(String email, String password, Listener listener) {
        mEmail = email;
        mPassword = password;
        mListener = listener;
    }

    private void fetchLoginUrl() throws IOException {
        Request request = new Request.Builder()
                .url("https://stackexchange.com/users/signin")
                .build();
        Response response = mClient.newCall(request).execute();
        //noinspection ConstantConditions
        mLoginUrl = response.body().string();
    }

    private void fetchNetworkFkey() throws IOException {
        Request request = new Request.Builder()
                .url(mLoginUrl)
                .build();
        Response response = mClient.newCall(request).execute();
        //noinspection ConstantConditions
        mNetworkFkey = jerry(response.body().string()).$("#fkey").attr("value");
    }

    private void fetchAuthUrl() throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("email", mEmail)
                .add("password", mPassword)
                .add("affId", "11")
                .add("fkey", mNetworkFkey)
                .build();
        Request request = new Request.Builder()
                .url("https://openid.stackexchange.com/affiliate/form/login/submit")
                .post(formBody)
                .build();
        Response response = mClient.newCall(request).execute();
        //noinspection ConstantConditions
        mAuthUrl = jerry(response.body().string()).$("noscript a").attr("href");
    }

    private boolean completeLogin() throws IOException {
        Request request = new Request.Builder()
                .url(mAuthUrl)
                .build();
        Response response = mClient.newCall(request).execute();
        if (response.request().url().encodedPath().equals("/account/prompt")) {
            //noinspection ConstantConditions
            Jerry doc = jerry(response.body().string());
            mSessionId = doc.$("input[name=session]").attr("value");
            mSessionFkey = doc.$("input[name=fkey]").attr("value");
            return false;
        }
        return true;
    }

    private boolean confirmOpenId() throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("session", mSessionId)
                .add("fkey", mSessionFkey)
                .build();
        Request request = new Request.Builder()
                .url("https://openid.stackexchange.com/account/prompt/submit")
                .post(formBody)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.request().url().encodedPath().equals("/");
    }

    @Override
    public void run() {
        try {

            // Perform the preliminary steps
            fetchLoginUrl();
            fetchNetworkFkey();
            fetchAuthUrl();

            // Complete the authentication and confirm the OpenID if necessary
            if (!completeLogin() && !confirmOpenId()) {
                throw new IOException("unable to complete login or confirm OpenID");
            }

            mListener.onSucceeded(mCookieJar.cookies());
        } catch (IOException e) {
            mListener.onFailed(e.getMessage());
        }
    }
}
