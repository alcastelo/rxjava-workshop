package com.nurkiewicz.rxjava;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.subscribers.TestSubscriber;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class R30_Zip {

	public static final Flowable<String> LOREM_IPSUM = Flowable.just("Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit");

	//el operador zip junta dos streams
	//con el zipwith junta dos floables
	//si hay de mas un valor que de otro, se ejecuta mientras hay elementos de los 2

	@Test
	public void gel(){
		Flowable<String> string = Flowable.just("A", "B", "C");
		Flowable<Integer> integer = Flowable.just(1,2,3);
		Flowable<String> var = string.zipWith(integer, (s, i) -> s + " x " + i);

		Single<List<String>> foo = var.toList();

		//convertimos el flowable a una lista
		List<String> lista=foo.blockingGet();
		System.out.println(lista);
	}


	@Test
	public void zipTwoStreams() throws Exception {
		//given
		Flowable<String> zipped = Flowable
				//aqui el zip, junta una array con un flowable rango
				.zip(
						LOREM_IPSUM,
						Flowable.range(1, 1_000),
						(word, num) -> word + "-" + num)
				.take(3);

		//when
		final TestSubscriber<String> subscriber = zipped.test();

		//then
		subscriber.assertComplete();
		subscriber.assertNoErrors();
		subscriber.assertValues("Lorem-1", "ipsum-2", "dolor-3");
	}

	/**
	 * Hint: Flowable.range(1, 3).repeat()
	 * Hint: Pair.of(...)
	 * Hint: filter()
	 */
	@Test
	public void everyThirdWord() throws Exception {
		//given
		Flowable<String> everyThirdWord = LOREM_IPSUM.skip(2)
				;

		//TODO aqui

/*
		Flowable<Pair<String, Integer>> pairs = everyThirdWord.zipWith(Flowable.range(1, 3).repeat(), Pair::of)
												.filter(pair-> pair.getRight()==3)
												.map(Pair::getLeft);
*/




		//when
		final TestSubscriber<String> subscriber = everyThirdWord.test();

		//then
		subscriber.assertValues("dolor", "consectetur");
	}

}
