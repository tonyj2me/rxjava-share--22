package cn.nextop.rxjava.share.practices;

import cn.nextop.rxjava.share.util.Lists;
import io.reactivex.Observable;
import org.junit.Test;

/**
 * @author Leon Chen
 * @since 1.0.0
 */
public class Practice5Test {

    @Test
    public void count() {
        new Practice5().count(Observable.just("a", "b", "c")).test().assertResult(3L);
    }

    @Test
    public void convert() {
        new Practice5().convert(Observable.just(Lists.of("a", "b", "c"))).test().assertResult("a", "b", "c");
    }

    @Test
    public void distinct() {
        new Practice5().distinct(Observable.just("a", "b", "b")).test().assertResult("a", "b");
    }

    @Test
    public void filter() {
        new Practice5().filter(Observable.just(1, 2, 3, 4, 5), x -> x > 2 && x < 5).test().assertResult(3, 4);
    }

    @Test
    public void elementAt() {
        new Practice5().elementAt(Observable.just("1", "2", "3", "4", "5"), 2).test().assertResult("3");
    }

    @Test
    public void repeat() {
        new Practice5().repeat(Observable.just("0", "1"), 5).test().assertResult("0", "1", "0", "1", "0", "1", "0", "1", "0", "1");
    }
}