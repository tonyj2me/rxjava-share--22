package cn.nextop.rxjava.share.practices;

import io.reactivex.Observable;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Baoyi Chen
 */
public class Practice4Test {

    @Test
    public void test() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        Map<String, Boolean> map = new ConcurrentHashMap<>();
        Observable<String> observable = Observable.create(emitter -> {
            new Practice4().runInMultiThread(Observable.just("a", "b", "c"), x -> {
                emitter.onNext(Thread.currentThread().getName());
            });
        });
        observable.subscribe(e -> {
            map.put(e, true);
            latch.countDown();
        });
        latch.await();
        assertTrue(map.size() >= 2);
    }
}