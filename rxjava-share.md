# Rx介绍
## ReactiveX的历史  
  
ReactiveX是Reactive Extensions的缩写，一般简写为Rx，最初是LINQ的一个扩展，由微软的架构师Erik Meijer领导的团队开发，在2012年11月开源，Rx是一个编程模型，目标是提供一致的编程接口，帮助开发者更方便的处理异步数据流，Rx库支持.NET、JavaScript和C++，现在已经支持几乎全部的流行编程语言，Rx的大部分语言库由ReactiveX这个组织负责维护，比较流行的有RxJava/RxJS/Rx.NET，社区网站是 reactivex.io。  

## 什么是ReactiveX  
  
微软给的定义是，Rx是一个函数库，让开发者可以利用可观察序列和LINQ风格查询操作符来编写异步和基于事件的程序，使用Rx，开发者可以用Observables表示异步数据流，用LINQ操作符查询异步数据流， 用Schedulers参数化异步数据流的并发处理，Rx可以这样定义：Rx = Observables + LINQ + Schedulers。  
  
ReactiveX.io给的定义是，Rx是一个使用可观察数据流进行异步编程的编程接口，ReactiveX结合了观察者模式、迭代器模式和函数式编程的精华。  
  
## 名词定义

* Observable:　可观察对象, 在观察者模式中是被观察的对象，一旦数据产生或发生变化，会通过某种方式通知观察者或订阅者。在异步环境下，也可以叫做生产者
* Observer  :  观察者对象，监听Observable发射的数据并做出响应，Subscriber是它的一个特殊实现。在异步环境下，也可以是Observable发射的数据的消费者，在其它的文档和场景里，有时我们也将Observer叫做Subscriber、Watcher、Reactor

## 概述
  
在ReactiveX中，一个观察者(Observer)订阅一个可观察对象(Observable)。观察者对Observable发射的数据或数据序列作出响应。这种模式可以极大地简化并发操作，因为它创建了一个回调，当数据准备完成时，apply这个回调。  
  
## Getting Start

* Observable示例

```java  
    
    // 创建可观察对象
    Observable<String> ob = Observable.just("1", "2", "3");
    
    // 对可观察对象进行变换，产生新的可观察对象
    Observable<Integer> ob1 = ob.map(e -> Integer.parseInt(e));
    
    // 创建观察者对象监听ob
    ob.subscribe(e-> {
        System.out.println(e + "," + e.getClass());
    });
    
    // 创建观察者对象监听ob1
    ob1.subscribe( e-> {
        System.out.println(e + "," + e.getClass());
    });
    
    // 执行结果
    // 1,class java.lang.String
    // 2,class java.lang.String
    // 3,class java.lang.String
    // 1,class java.lang.Integer
    // 2,class java.lang.Integer
    // 3,class java.lang.Integer

```

* Single与Maybe示例

```java  
    // Single也是一种Observable，区别是只emit一个值或错误
    Single<String> ob = Single.just("abc");
    
    // Maybe也是一种Observable，区别是有可能不emit值或者emit一个值或错误
    Maybe<String> maybe = Maybe.empty();

```

* Subject示例

```java  
    // Subject既是一个Observable又是一个Observer, 也可以在既有的Observable和Observer中间当作代理
    
    // Subject作为Observable的例子
    Subject<String> subject = PublishSubject.create();
    subject.subscribe(e -> System.out.println(e));
    subject.onNext("a");
    subject.onNext("b");
    subject.onNext("c");
    
    // Subject作为Observer的例子
    Subject<String> subject = PublishSubject.create(); // 1
    subject.subscribe(e -> System.out.println(e));     // 2
    
    Observable.just("1", "2", "3").subscribe(subject); //真正干活的代码在2 处

```

* Flowable示例

```java  
    // Flowable和Observable很像，区别就是Flowable带背压处理，Observable不带，稍后详解
    Flowable.just(1,2,3);
```

* 操作符
  
在rxjava中， Observable, Flowable, Subject, Single, Maybe 类的静态方法都叫做操作符, 根据作用的不同，操作符也分几类

### 操作符分类

#### 创建操作
用于创建Observable的操作符  
  
create — 通过调用观察者的方法从头创建一个Observable  
defer — 在观察者订阅之前不创建这个Observable，为每一个观察者创建一个新的Observable  
empty/never/error — 创建行为受限的特殊Observable  
fromArray/fromFuture/fromIterable — 将其它的对象或数据结构转换为Observable  
interval — 创建一个定时发射整数序列的Observable  
just — 将对象或者对象集合转换为一个会发射这些对象的Observable  
range — 创建发射指定范围的整数序列的Observable  
repeat — 创建重复发射特定的数据或数据序列的Observable  
timer — 创建在一个指定的延迟之后发射单个数据的Observable  
  
#### 变换操作  
这些操作符可用于对Observable发射的数据进行变换，详细解释可以看每个操作符的文档  
  
buffer — 缓存，可以简单的理解为缓存，它定期从Observable收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个  
flatMap — 扁平映射，将Observable发射的数据变换为Observables集合，然后将这些Observable发射的数据平坦化的放进一个单独的Observable，可以认为是一个将嵌套的数据结构展开的过程。  
groupBy — 分组，将原来的Observable分拆为Observable集合，将原始Observable发射的数据按Key分组，每一个Observable发射一组不同的数据  
map — 映射，通过对序列的每一项都应用一个函数变换Observable发射的数据，实质是对序列中的每一项执行一个函数，函数的参数就是这个数据项  
scan — 扫描，对Observable发射的每一项数据应用一个函数，然后按顺序依次发射这些值  
window — 窗口，定期将来自Observable的数据分拆成一些Observable窗口，然后发射这些窗口，而不是每次发射一项。类似于buffer，但buffer发射的是数据，Window发射的是Observable，每一个Observable发射原始Observable的数据的一个子集  
  
#### 过滤操作
这些操作符用于从Observable发射的数据中进行选择  
  
debounce — 只有在空闲了一段时间后才发射数据，通俗的说，就是如果一段时间没有操作，就执行一次操作  
distinct — 去重，过滤掉重复数据项  
elementAt — 取值，取特定位置的数据项  
filter — 过滤，过滤掉没有通过谓词测试的数据项，只发射通过测试的  
first — 首项，只发射满足条件的第一条数据  
ignoreElements — 忽略所有的数据，只保留终止通知(onError或onCompleted)  
last — 末项，只发射最后一条数据  
sample — 取样，定期发射最新的数据，等于是数据抽样，有的实现里叫throttleFirst  
skip — 跳过前面的若干项数据  
skipLast — 跳过后面的若干项数据  
take — 只保留前面的若干项数据  
takeLast — 只保留后面的若干项数据  
  
#### 组合操作  
组合操作符用于将多个Observable组合成一个单一的Observable  
  
combineLatest — 当两个Observables中的任何一个发射了一个数据时，通过一个指定的函数组合每个Observable发射的最新数据（一共两个数据），然后发射这个函数的结果  
join — 无论何时，如果一个Observable发射了一个数据项，只要在另一个Observable发射的数据项定义的时间窗口内，就将两个Observable发射的数据合并发射  
merge — 将两个Observable发射的数据组合并成一个  
startWith — 在发射原来的Observable的数据序列之前，先发射一个指定的数据序列或数据项  
zipWith — 打包，使用一个指定的函数将多个Observable发射的数据组合在一起，然后将这个函数的结果作为单项数据发射  
  
#### 错误处理
这些操作符用于从错误通知中恢复  

onErrorReturn —   
onErrorResumeNext —   
onErrorResumeItem —   
onErrorReturnItem —   
retry —   
retryUntil —   
retryWhen —   
  
#### 辅助操作
一组用于处理Observable的操作符  
  
delay — 延迟一段时间发射结果数据  
doAfterNext/doAfterTerminate/doFinally/doOnXxx — 注册一个回调到Observable的生命周期事件  
observeOn — 指定观察者调度程序上执行  
serialize — 强制Observable按次序发射数据  
subscribe — 收到Observable发射的数据和通知后执行的操作  
subscribeOn — 指定Observable应该在哪个调度程序上执行  
timeInterval — 将一个Observable转换为发射两个数据之间所耗费时间的Observable  
timeout — 添加超时机制，如果过了指定的一段时间没有发射数据，就发射一个错误通知  
timestamp — 给Observable发射的每个数据项添加一个时间戳  
using — 创建一个只在Observable的生命周期内存在的一次性资源  
  
#### 条件和布尔操作  
这些操作符可用于单个或多个数据项，也可用于Observable  
  
all — 判断Observable发射的所有的数据项是否都满足某个条件  
ambWith — 给定多个Observable，只让第一个发射数据的Observable发射全部数据  
contains — 判断Observable是否会发射一个指定的数据项  
defaultIfEmpty — 发射来自原始Observable的数据，如果原始Observable没有发射数据，就发射一个默认数据  
sequenceEqual — 判断两个Observable是否按相同的数据序列  
skipUntil — 丢弃原始Observable发射的数据，直到第二个Observable发射了一个数据，然后发射原始Observable的剩余数据  
skipWhile — 丢弃原始Observable发射的数据，直到一个特定的条件为假，然后发射原始Observable剩余的数据  
takeUntil — 发射来自原始Observable的数据，直到第二个Observable发射了一个数据或一个通知  
takeWhile — 发射原始Observable的数据，直到一个特定的条件为真，然后跳过剩余的数据  
  
#### 算术和聚合操作
这些操作符可用于整个数据序列  
  
concat — 不交错的连接多个Observable的数据  
count — 计算Observable发射的数据个数，然后发射这个结果  
reduce — 按顺序对数据序列的每一个应用某个函数，然后返回这个值  
  
#### 连接操作
一些有精确可控的订阅行为的特殊Observable  
  
connect — 指示一个可连接的Observable开始发射数据给订阅者  
publish — 将一个普通的Observable转换为可连接的  
refCount — 使一个可连接的Observable表现得像一个普通的Observable  
replay — 确保所有的观察者收到同样的数据序列，即使他们在Observable开始发射数据之后才订阅  
  
#### 转换操作
toList/toXxx — 将Observable转换为其它的对象或数据结构  
blockingGet/blockingWait 阻塞Observable的操作符  
  
#### 操作符决策树
几种主要的需求  
  
直接创建一个Observable（创建操作）  
组合多个Observable（组合操作）  
对Observable发射的数据执行变换操作（变换操作）  
从Observable发射的数据中取特定的值（过滤操作）  
转发Observable的部分值（条件/布尔/过滤操作）  
对Observable发射的数据序列求值（算术/聚合操作）  

# 由观察者模式演变到rxjava

```java  
    // 观察者模式
    public class Button {
        List<ButtonListener> listeners = new CopyOnWriteArrayList<>();

        public void addListener(ButtonListener listener) {
            this.listeners.add(listener);
        }

        public void delListener(ButtonListener listener) {
            this.listeners.remove(listener);
        }

        protected void notifyOnClick(int event) {
            for (ButtonListener listener : listeners) {
                listener.onClick(event);
            }
        }

        public void click(int event) {
            notifyOnClick(event);
        }
    }

    public interface ButtonListener {
        void onClick(int event);
    }

    public static void main(String[] args) {
        Button button = new Button();
        button.addListener(System.out::println);
        for (int i = 0; i < 10; i++) {
            button.click(i);
        }
    }

```

* 如果上述代码属于第三方库，不能随意更改源码，那么进行如下扩展使其转化成rxjava风格

```java  
    // 对上述代码进行改造
    Button r = new Button();
    Observable<Integer> buttonObservable = Observable.create(s -> {
        ButtonListener listener = s::onNext;
        r.addListener(listener);
        s.setCancellable(() -> r.delListener(listener));
    });
    
    // 订阅上述observable，并在适当的时机取消订阅
    Disposable d1 = buttonObservable.subscribe(System.out::println);
    Disposable d2 = null;
    for (int i = 0; i < 100; i++) {
        r.click(i);
        if (i == 50) {
            d1.dispose();
            d2 = buttonObservable.subscribe(System.out::println);
        }
    }
    if (d2 != null) d2.dispose();

```

* 如果可以随意更改源码，那么进行如下重构减少相应的代码量

```java  
    // 对上述代码进行改造
    public class Button {
        private final Subject<Integer> subject;
    
        public Button(Subject<Integer> subject) {
            this.subject = subject;
        }
    
        public void click(int event) {
            subject.onNext(event);
        }
    }
    
    // 订阅上述Button，并在适当的时机取消订阅
    Subject<Integer> subject = PublishSubject.create();
    Disposable d1 = subject.subscribe(System.out::println);
    Disposable d2 = null;
    Button r = new Button(subject);
    for (int i = 0; i < 100; i++) {
        r.click(i);
        if (i == 50) {
            d1.dispose();
            d2 = subject.subscribe(System.out::println);
        }
    }
    if (d2 != null) d2.dispose();
```

* 异步订阅

```java  
    Subject<Integer> subject = PublishSubject.create();
    subject.observeOn(Schedulers.io()).subscribe(e -> System.out.println("a" + e));
    subject.observeOn(Schedulers.io()).subscribe(e -> System.out.println("b" + e));
    Button r = new Button(subject);
    for (int i = 0; i < 100; i++) {
        r.click(i);
    }
    
    // 部分执行结果
    // a0
    // a1
    // a2
    // a3
    // a4
    // a5
    // a6
    // a7
    // b0
    // a8
    // b1
    // a9
    // b2
    // a10
    // b3
    // a11
    // b4
    // a12
    // b5
    
    //等同于
    protected void notifyOnClick(int event) {
        for (ButtonListener listener : listeners) {
            //提交event到这个listener所在的线程
            executors[shard(listener)].submit(() -> {
                listener.onClick(event);
            });
        }
    }
    
    // 另一种异步订阅
    Subject<Integer> subject = PublishSubject.create();
    
    Subject<Integer> subSubject = PublishSubject.create();
    subSubject.subscribe(e -> System.out.println("a" + e + "," + Thread.currentThread()));
    subSubject.subscribe(e -> System.out.println("b" + e + "," + Thread.currentThread()));

    subject.observeOn(Schedulers.io()).subscribe(subSubject);
    Button r = new Button(subject);
    for (int i = 0; i < 100; i++) {
        r.click(i);
    }
    
    // 部分结果
    // a0,Thread[RxNewThreadScheduler-1,5,main]
    // b0,Thread[RxNewThreadScheduler-1,5,main]
    // a1,Thread[RxNewThreadScheduler-1,5,main]
    // b1,Thread[RxNewThreadScheduler-1,5,main]
    // a2,Thread[RxNewThreadScheduler-1,5,main]
    // b2,Thread[RxNewThreadScheduler-1,5,main]
    // a3,Thread[RxNewThreadScheduler-1,5,main]
    // b3,Thread[RxNewThreadScheduler-1,5,main]
    // a4,Thread[RxNewThreadScheduler-1,5,main]
    // b4,Thread[RxNewThreadScheduler-1,5,main]
    // a5,Thread[RxNewThreadScheduler-1,5,main]
    // b5,Thread[RxNewThreadScheduler-1,5,main]
    // a6,Thread[RxNewThreadScheduler-1,5,main]
    // b6,Thread[RxNewThreadScheduler-1,5,main]
    
    //等同于
    protected void notifyOnClick(int event) {
        singleThreadExecutor.submit(() -> {
            for (ButtonListener listener : listeners) {
                listener.onClick(event);
            }
        });
    }

```

* 副作用1  

```java  

    Observable<Character> ob = Observable.range(1,3).map(x -> (char)(x+65));
    AtomicInteger index = new AtomicInteger(0);
    ob.subscribe(x -> {
        System.out.println("receive " + x + " at " + index.incrementAndGet());
    });
    
    // 结果
    // receive B at 1
    // receive C at 2
    // receive D at 3
    
    // 当订阅者唯一的时候，这个index看起来是正确的,但是增加了订阅者，这个index的结果并不正确
    
    Observable<Character> ob = Observable.range(1,3).map(x -> (char)(x+65));
    AtomicInteger index = new AtomicInteger(0);
    ob.subscribe(x -> {
        System.out.println("receive " + x + " at " + index.incrementAndGet());
    });
    ob.subscribe(x -> {
        System.out.println("receive " + x + " at " + index.incrementAndGet());
    });
    // 结果
    // receive B at 1
    // receive C at 2
    // receive D at 3
    // receive B at 4
    // receive C at 5
    // receive D at 6
    
    // 避免副作用的一种方式
    Observable<Tuple2<Integer, Character>> ob = Observable.range(1,3).map(x -> new Tuple2<>(1, (char)(x+65))).scan((acc, value) -> {
        return new Tuple2<>(acc.t1 + 1, value.t2);
    });
    ob.subscribe(x -> {
        System.out.println("receive " + x.t2 + " at " + x.t1);
    });
    ob.subscribe(x -> {
        System.out.println("receive " + x.t2 + " at " + x.t1);
    });
    
    // 结果
    // receive B at 1
    // receive C at 2
    // receive D at 3
    // receive B at 1
    // receive C at 2
    // receive D at 3
    
```

* 副作用2

```java  

    Observable<Tuple2<String, String>> ob = Observable.just(new Tuple2<>("hello", "world"));
    ob.subscribe(x -> x.t2 = "bob"); //在此订阅中更改了t2的值，产生了副作用，影响其他的订阅
    ob.subscribe(e -> System.out.println(e.t1 + " " + e.t2));
    
    // 结果
    // hello bob
```