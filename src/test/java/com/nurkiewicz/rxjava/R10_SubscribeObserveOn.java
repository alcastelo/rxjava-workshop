package com.nurkiewicz.rxjava;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nurkiewicz.rxjava.util.Sleeper;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Ignore
public class R10_SubscribeObserveOn {

    private static final Logger log = LoggerFactory.getLogger(R10_SubscribeObserveOn.class);

    @Test
    public void subscribeOn() throws Exception {
        Flowable<BigDecimal> obs = slowFromCallable();

        obs
                .subscribeOn(Schedulers.io())    ///operador para  concurrencia
                ///nos subscribe usando este scheduler
                .subscribe(
                        x -> log.info("Got: {}", x)
                );
        log.info("Subscrito");
        Sleeper.sleep(ofMillis(1_100));
        log.info("Fin");
    }

    @Test
    public void subscribeOnForEach() throws Exception {
        Flowable<BigDecimal> obs = slowFromCallable();

        obs
                .subscribeOn(Schedulers.io())
                .subscribe(
                        x -> log.info("Got: {}", x)
                );
        Sleeper.sleep(ofMillis(1_100));
    }

    private Flowable<BigDecimal> slowFromCallable() {
        return Flowable.fromCallable(() -> {    //crea el emisor desde el callable, emite lo que se genera adentro del lambda ahora el crear el emisor y meterle el just
            log.info("Starting");
            Sleeper.sleep(ofSeconds(1));
            log.info("Done");
            return BigDecimal.TEN;
        });
    }

    @Test
    public void observeOn() throws Exception {
        slowFromCallable()
                .subscribeOn(Schedulers.io())   //unbounded queue
                .doOnNext(x -> log.info("A: {}", x)) //modifica el publicador para que ejecute esto en el onnext
                               //(Schedulers.computation mismos hilos que cores el procesador)
                .observeOn(Schedulers.computation())// (Schedulers.computation mismos hilos que cores el procesador) al poner un observe on, el doOnNext se ejecuta en el hilo que le estemos diciendo
                .doOnNext(x -> log.info("B: {}", x))
                //Schedulers.newThread() crea un hilo cuando lo necesita
                .observeOn(Schedulers.newThread())//al poner el observer lo que venga debajo automaticamente cambia de hilo al indicado
                .doOnNext(x -> log.info("C: {}", x))
                .subscribe(
                        x -> log.info("Got: {}", x)
                );
        Sleeper.sleep(ofMillis(1_100));
    }

    /**
     * TODO Create CustomExecutor
     */
    @Test
    public void customExecutor() throws Exception {
        final TestSubscriber<BigDecimal> subscriber = slowFromCallable()
                .subscribeOn(myCustomScheduler())
                .test();
        await().until(() -> {
                    Thread lastSeenThread = subscriber.lastThread();
                    assertThat(lastSeenThread).isNotNull();
                    assertThat(lastSeenThread.getName()).startsWith("CustomExecutor-");
                }
        );
    }

    /**
     * Hint: Schedulers.from()
     * Hint: ThreadFactoryBuilder
     */
    private Scheduler myCustomScheduler() {
        final ThreadFactory factoryBuilder = new ThreadFactoryBuilder().setNameFormat("CustomExecutor-%d").build();
        final ExecutorService service = Executors.newFixedThreadPool(10, factoryBuilder);
        return Schedulers.from(service);
    }
/*
    private Scheduler myCustomScheduler() {
        return Schedulers.io();
    }
*/


}
