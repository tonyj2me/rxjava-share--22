/*
 * Copyright 2016-2017 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nextop.rxjava.share.examples;

import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

import java.io.IOException;

/**
 * @author Baoyi Chen
 */
public class Example1 {
    public void example0() {

    }

    public static class Tuple2<T1, T2> {
        public T1 t1;
        public T2 t2;

        public Tuple2(T1 t1, T2 t2) {
            this.t1 = t1;
            this.t2 = t2;
        }
    }

    public static void main(String[] args) throws IOException {
        Observable.just(1, 2, 3).concatWith(Observable.error(new Exception())).concatWith(Observable.just(4, 5, 6)).onErrorResumeNext(Observable.empty()).subscribe(System.out::println, e -> {});
    }

    // 对上述代码进行改造
    public static class Button {
        private final Subject<Integer> subject;

        public Button(Subject<Integer> subject) {
            this.subject = subject;
        }

        public void click(int event) {
            subject.onNext(event);
        }
    }

}
