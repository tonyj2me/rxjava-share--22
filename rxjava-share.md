# rxjava简介

# Getting start

WIP

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
    subject.observeOn(Schedulers.newThread()).subscribe(e -> System.out.println("a" + e));
    subject.observeOn(Schedulers.newThread()).subscribe(e -> System.out.println("b" + e));
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

    subject.observeOn(Schedulers.newThread()).subscribe(subSubject);
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