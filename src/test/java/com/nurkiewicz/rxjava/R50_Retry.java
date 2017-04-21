package com.nurkiewicz.rxjava;

import com.nurkiewicz.rxjava.util.CloudClient;
import io.reactivex.Flowable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.LongAdder;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Ignore
public class R50_Retry {

    private static final Logger log = LoggerFactory.getLogger(R50_Retry.class);
    private CloudClient cloudClient = mock(CloudClient.class);

    public static void main(String[] args) {
        Flowable<Integer> numero = Flowable.just(1, 2, 3);
        Flowable<Integer> error = Flowable.error(new RuntimeException("jar"));
        Flowable<Integer> ints = numero.concatWith(error);

        ints
                //eejecuta el lambda cuando hay un error
                .doOnError(err -> log.warn("error", err))
                //sustituye el error por un elemento
                .onErrorReturnItem(42)
                //un retry obliga a reintentar algo
                //.retry(3)
                //en la subscripcion mandamos un lambda por cada uno da las situaciones, elemento, error y completo
                .subscribe(
                        x -> log.info("Evento {}", x),
                        err -> log.error("error", err),
                        () -> log.info("Completado")

                );


    }

    /**
     * Hint: retry(int)
     * Hint: doOnError(), doOnSubscribe() for logging
     */
    @Test
    public void shouldRetryThreeTimes() throws Exception {
        //given
        LongAdder subscriptionCounter = new LongAdder();
        given(cloudClient.pricing()).willReturn(
                failure()
                        .doOnSubscribe(disposable -> subscriptionCounter.increment())
        );

        //when
        cloudClient
                .pricing()
                .doOnSubscribe(err->log.info("on subscribe"))
                .doOnError(err->log.info("error",err))
                .doOnComplete(()->log.info("Complete"))
                .retry(3)

                //.retry(ex->ex instanceof IOException)
                .test();


        //then
        await().until(() -> subscriptionCounter.sum() == 4);
    }

    private Flowable<BigDecimal> failure() {
        return Flowable.error(new RuntimeException("Simulated"));
    }


}
