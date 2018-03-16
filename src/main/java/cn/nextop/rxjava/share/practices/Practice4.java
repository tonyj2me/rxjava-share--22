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

package cn.nextop.rxjava.share.practices;


import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


/**
 * @author Baoyi Chen
 */
public class Practice4 {

    /*
     * 举例:
     * 参数observable = Observable["a", "b", "c"]
     * 参数observer在消费observable时，每个元素都在独立的线程
     *          "a"  -->  "b"  -->  "c"
     *           |         |         |
     *        thread1   thread2    thread3
     *
     */
    public void runInMultiThread(Observable<String> observable, Consumer<String> observer) {
        throw new UnsupportedOperationException("implementation");
    }

}
