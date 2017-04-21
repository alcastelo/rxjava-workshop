package com.nurkiewicz.rxjava;

import io.reactivex.Flowable;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.nurkiewicz.rxjava.R30_Zip.LOREM_IPSUM;

@Ignore
public class R31_WindowBuffer {


    public static void main(String[] args) {
        final Flowable<Integer> range = Flowable.range(1, 10);
        //agrupa los valores de 3 en 3
        //Flowable<List<Integer>> var = range.buffer(3 );
        //cuando tiene un skip, hace grupos de 3 pero los grupos empiezan dos elementos despues osea 123, 345, 567, 789 ,910 si el valor es mayor que el tamaño del grupo se descartan elementos

/*		Flowable<List<Integer>> var = range.buffer(3 ,2);
        var.subscribe(System.out::println);*/



/*
		Flowable
				//se genera un valor cada 30 milisegundos
				.interval(30, TimeUnit.MILLISECONDS)
				//se hace un buffer, para que se gener 1 por segundo , resumiendo todo
				.buffer(1,TimeUnit.SECONDS)
				//.map(list->list.size()) //esto nos daria el tamaño de array que se devuelve cada segundo
				//.count()//espera a que se terminen los eventos
				.subscribe(System.out::println);
*/

        Flowable<Long> var = Flowable
                //se genera un valor cada 30 milisegundos
                .interval(30, TimeUnit.MILLISECONDS)
                //se hace un buffer, para que se gener 1 por segundo , resumiendo todo
                //windows devuelve un flowable en vez de una lista
                .window(1, TimeUnit.SECONDS)
                .flatMap((Flowable<Long> obs) -> obs.count().toFlowable()) //esto nos daria el tamaño de array que se devuelve cada segundo
                ;


        //hacemos el sleep porque si no el programa termina y no saca nada
        try {
            TimeUnit.SECONDS.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hint: use buffer()
     */
    @Test
    public void everyThirdWordUsingBuffer() throws Exception {
        //given
        //Flowable<String> everyThirdWord = LOREM_IPSUM;

        Flowable<String> everyThirdWord = LOREM_IPSUM
                .skip(2)
                .buffer(1, 3)
                .map(list -> list.get(0));

        //tambien vale
/*
		Flowable<String> everyThirdWord = LOREM_IPSUM
											.buffer(3)
											.filter(list->list.size()==3)
											.map(list->list.get(2));
*/


        //then
        everyThirdWord
                .test()
                .assertValues("dolor", "consectetur");
    }

    /**
     * Hint: use window()
     * Hint: use elementAt()
     */
    @Test
    public void everyThirdWordUsingWindow() throws Exception {
        //given
        Flowable<String> everyThirdWord = LOREM_IPSUM
                .window(3)
                .flatMapMaybe(f->f.elementAt(2));
                //.flatMap((Flowable<String> obs) -> obs.skip(2));   //tambien vale

        //then
        everyThirdWord

                .test()
                .assertValues("dolor", "consectetur");
    }

}
