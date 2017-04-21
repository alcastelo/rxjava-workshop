package com.nurkiewicz.rxjava;

import io.reactivex.Flowable;
import org.reactivestreams.Subscriber;

/**
 * Created by angel on 20/04/17.
 */
public class Contenedor extends Flowable<Contenedor> {
    String Uri;
    String Html;


    public Contenedor(String uri, String html) {
        Uri = uri;
        Html = html;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getHtml() {
        return String.valueOf(Html);
    }

    public void setHtml(String html) {
        Html = html;
    }

    @Override
    protected void subscribeActual(Subscriber<? super Contenedor> s) {

    }
}
