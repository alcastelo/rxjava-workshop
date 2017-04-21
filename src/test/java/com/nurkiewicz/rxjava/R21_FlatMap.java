package com.nurkiewicz.rxjava;

import com.nurkiewicz.rxjava.util.UrlDownloader;
import com.nurkiewicz.rxjava.util.Urls;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class R21_FlatMap {

    /**
     * Hint: UrlDownloader.download()
     * Hint: flatMap(), maybe concatMap()?
     * Hint: Pair.of(...)
     * Hint: Flowable.toMap()
     */
    @Test
    public void shouldDownloadAllUrls() throws Exception {
        //given
        Flowable<URL> urls = Urls.all();


/*        final Flowable<String> htmls2 = urls
                .flatMap(url -> UrlDownloader.download(url)
                        .subscribeOn(Schedulers.io())); //ojo al subscribeon


        Flowable<Pair<String, String>> gel = urls.map(s -> {
            final String[] html = {""};
            final String[] gelote = new String[1];

            UrlDownloader.download(s).subscribe(value -> {
                gelote[0] = value;
            });
            String result = gelote[0];
            return Pair.of(s.toURI().toString(), gelote[0]);
        });

        //
        Flowable<String> htmls =
                urls.flatMap(url -> UrlDownloader.download(url).subscribeOn(Schedulers.io()));

        Flowable<Pair<URL, Flowable<String>>> broken = urls.map(url -> Pair.of(url, UrlDownloader.download(url)));

        urls.flatMap(url -> {
            final Flowable<String> html = UrlDownloader.download(url);
            Flowable<Pair<URL,String>> var =html.map(htmlstr->Pair.of(url,htmlstr));
            return var;
        });

        */
        Flowable<Pair<URI, String>> pairs = urls.flatMap(url -> UrlDownloader.download(url)
                .subscribeOn(Schedulers.io())
                .map(htmlstr -> Pair.of(toUri(url), htmlstr)));


        //no confundir toMap con map
        //Maybe: 0..1 values
        //Single 1 value
        //Completable 0 values
        //Flowable 0...infinity
        //single es un stream que solo tiene un valor
        Single<Map<URI, String>> var = pairs.toMap((Pair<URI, String> pair) -> pair.getLeft(), (Pair<URI, String> pair) -> pair.getRight());
        Map<URI, String> bodies2 = var.blockingGet();
        Map<URI, String> bodies = pairs.toMap(Pair::getLeft, Pair::getRight).blockingGet();
        TimeUnit.SECONDS.sleep(20);

        //when
        //WARNING: URL key in HashMap is a bad idea here

        //No mutar el mapa del suscribe , en vez de eso, use toMap()
        //con confundir con el operador map()
        //blocking*


        /*Map<URI, String> bodies = new HashMap<>();*/

/*
        gel.toMap(s.getLeft());
        gel.subscribe(System.out::println);
*/

        //then
        assertThat(bodies).hasSize(996);
        assertThat(bodies).containsEntry(new URI("http://www.twitter.com"), "<html>www.twitter.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.aol.com"), "<html>www.aol.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.mozilla.org"), "<html>www.mozilla.org</html>");
    }


    /**
     * Hint: flatMap with int parameter
     */
    @Test
    public void downloadThrottled() throws Exception {
        //given
        Flowable<URL> urls = Urls.all().take(20);
        //ojo al parametro del flatmap que indica el maximo de concurrencia
        Flowable<Pair<URI, String>> pairs = urls.flatMap(url -> UrlDownloader.downloadThrottled(url)
                .subscribeOn(Schedulers.io())
                .map(htmlstr -> Pair.of(toUri(url), htmlstr)), 10);
        Single<Map<URI, String>> var = pairs.toMap((Pair<URI, String> pair) -> pair.getLeft(), (Pair<URI, String> pair) -> pair.getRight());
        Map<URI, String> bodies2 = var.blockingGet();
        Map<URI, String> bodies = pairs.toMap(Pair::getLeft, Pair::getRight).blockingGet();


        Map<URI, String> bodies3 = urls.flatMap(url -> UrlDownloader.downloadThrottled(url)
                .subscribeOn(Schedulers.io())
                .map(htmlstr -> Pair.of(toUri(url), htmlstr)), 10).toMap(Pair::getLeft, Pair::getRight).blockingGet();

        //when
        //Use UrlDownloader.downloadThrottled()


        //then
        assertThat(bodies).containsEntry(new URI("http://www.twitter.com"), "<html>www.twitter.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.adobe.com"), "<html>www.adobe.com</html>");
        assertThat(bodies).containsEntry(new URI("http://www.bing.com"), "<html>www.bing.com</html>");
    }

    private URI toUri(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


}
