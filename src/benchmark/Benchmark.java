package Benchmark;

import list.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * FASE 1: MEDICIÓN DE DATOS - SOLO RECOLECTA TIEMPOS
 * ====================================================
 * Este programa ÚNICAMENTE mide el tiempo de ejecución.
 * NO realiza graficación alguna (ver graficarResultados.m después).
 * 
 * Tamaños probados: 10, 100, 1_000, 10_000, 100_000, 1_000_000
 * 
 * NOTA: SinglyLinkedList.pushBack y SinglyLinkedList.popBack son O(n²)
 * por eso se limitan a n <= 10_000 para evitar tiempos excesivos.
 */

public class Benchmark {

    static class Result {
        String impl;
        String method;
        int n;
        long timeNs;

        Result(String impl, String method, int n, long timeNs) {
            this.impl = impl;
            this.method = method;
            this.n = n;
            this.timeNs = timeNs;
        }

        @Override
        public String toString() {
            double us = timeNs / 1_000.0;
            return String.format("%s,%s,%d,%d,%.3f", impl, method, n, timeNs, us);
        }
    }

    static final int[] SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000};
    static final int WARMUP = 2;
    static final int ITERATIONS = 5;

    public static void main(String[] args) throws Exception {
        long startTotal = System.currentTimeMillis();

        System.out.println("📊 PARTE 1: Benchmarking List Methods (pushFront, popFront, pushBack, popBack, find)...");
        java.util.List<Result> resultsPart1 = new ArrayList<>();
        resultsPart1.addAll(benchmarkPushFront());
        resultsPart1.addAll(benchmarkPopFront());
        resultsPart1.addAll(benchmarkPushBack());
        resultsPart1.addAll(benchmarkPopBack());
        resultsPart1.addAll(benchmarkFind());
        saveResultsToCSV("results_list_part1.csv", resultsPart1);

        System.out.println("📊 PARTE 2: Benchmarking Position-based Methods (addBefore, addAfter, erase)...");
        java.util.List<Result> resultsPart2 = new ArrayList<>();
        resultsPart2.addAll(benchmarkAddBefore());
        resultsPart2.addAll(benchmarkAddAfter());
        resultsPart2.addAll(benchmarkErase());
        saveResultsToCSV("results_list_part2.csv", resultsPart2);

        System.out.println("📊 PARTE 3: Benchmarking Stack and Queue...");
        java.util.List<Result> resultsPart3 = new ArrayList<>();
        resultsPart3.addAll(benchmarkStackPush());
        resultsPart3.addAll(benchmarkStackPop());
        resultsPart3.addAll(benchmarkQueueEnqueue());
        resultsPart3.addAll(benchmarkQueueDequeue());
        saveResultsToCSV("results_stack_queue.csv", resultsPart3);

        long endTotal = System.currentTimeMillis();
        System.out.println("\n✅ MEDICIÓN COMPLETADA EN " + (endTotal - startTotal) + " ms");
        System.out.println("📁 Archivos generados:");
        System.out.println("   - results_list_part1.csv");
        System.out.println("   - results_list_part2.csv");
        System.out.println("   - results_stack_queue.csv");
        System.out.println("\n⚠️  SIGUIENTE PASO: Ejecutar graficarResultados.m en MATLAB");
    }

    static java.util.List<Result> benchmarkPushFront() {
        System.out.println("\n⏱️  PushFront (esperado: O(1))");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            List<Integer> sll = new SinglyLinkedList<>();
            long time1 = measureAvg(() -> {
                for (int i = 0; i < n; i++) sll.pushFront(i);
            }, n);
            results.add(new Result("SinglyLinkedList", "pushFront", n, time1));
            System.out.print(".");

            List<Integer> sllwt = new SinglyLinkedListWithTail<>();
            long time2 = measureAvg(() -> {
                for (int i = 0; i < n; i++) sllwt.pushFront(i);
            }, n);
            results.add(new Result("SinglyLinkedListWithTail", "pushFront", n, time2));
            System.out.print(".");

            List<Integer> dll = new DoublyLinkedList<>();
            long time3 = measureAvg(() -> {
                for (int i = 0; i < n; i++) dll.pushFront(i);
            }, n);
            results.add(new Result("DoublyLinkedList", "pushFront", n, time3));
            System.out.print(".");

            List<Integer> dllwt = new DoublyLinkedListWithTail<>();
            long time4 = measureAvg(() -> {
                for (int i = 0; i < n; i++) dllwt.pushFront(i);
            }, n);
            results.add(new Result("DoublyLinkedListWithTail", "pushFront", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static java.util.List<Result> benchmarkPopFront() {
        System.out.println("⏱️  PopFront (esperado: O(1))");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            long time1 = benchmarkPopFrontImpl(SinglyLinkedList::new, n);
            results.add(new Result("SinglyLinkedList", "popFront", n, time1));
            System.out.print(".");

            long time2 = benchmarkPopFrontImpl(SinglyLinkedListWithTail::new, n);
            results.add(new Result("SinglyLinkedListWithTail", "popFront", n, time2));
            System.out.print(".");

            long time3 = benchmarkPopFrontImpl(DoublyLinkedList::new, n);
            results.add(new Result("DoublyLinkedList", "popFront", n, time3));
            System.out.print(".");

            long time4 = benchmarkPopFrontImpl(DoublyLinkedListWithTail::new, n);
            results.add(new Result("DoublyLinkedListWithTail", "popFront", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static long benchmarkPopFrontImpl(ListFactory factory, int n) {
        long avgNs = 0;
        int warmup = (n > 10_000) ? 1 : WARMUP;
        int iterations = (n > 10_000) ? 2 : ITERATIONS;

        for (int w = 0; w < warmup; w++) {
            List<Integer> list = factory.create();
            for (int i = 0; i < n; i++) list.pushFront(i);
            for (int i = 0; i < n; i++) list.popFront();
        }

        for (int iter = 0; iter < iterations; iter++) {
            List<Integer> list = factory.create();
            for (int i = 0; i < n; i++) list.pushFront(i);

            long start = System.nanoTime();
            for (int i = 0; i < n; i++) list.popFront();
            avgNs += (System.nanoTime() - start);
        }
        return avgNs / iterations;
    }

    static java.util.List<Result> benchmarkPushBack() {
        System.out.println("⏱️  PushBack (esperado: O(n) vs O(1) con tail)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            // SinglyLinkedList es O(n²), solo medir hasta 100
            if (n <= 1000) {
                List<Integer> sll = new SinglyLinkedList<>();
                long time1 = measureAvg(() -> {
                    for (int i = 0; i < n; i++) sll.pushBack(i);
                }, n);
                results.add(new Result("SinglyLinkedList", "pushBack", n, time1));
            } else {
                System.out.print("⊘");  // Indicador de que se saltó
            }
            System.out.print(".");

            List<Integer> sllwt = new SinglyLinkedListWithTail<>();
            long time2 = measureAvg(() -> {
                for (int i = 0; i < n; i++) sllwt.pushBack(i);
            }, n);
            results.add(new Result("SinglyLinkedListWithTail", "pushBack", n, time2));
            System.out.print(".");

            List<Integer> dll = new DoublyLinkedList<>();
            long time3 = measureAvg(() -> {
                for (int i = 0; i < n; i++) dll.pushBack(i);
            }, n);
            results.add(new Result("DoublyLinkedList", "pushBack", n, time3));
            System.out.print(".");

            List<Integer> dllwt = new DoublyLinkedListWithTail<>();
            long time4 = measureAvg(() -> {
                for (int i = 0; i < n; i++) dllwt.pushBack(i);
            }, n);
            results.add(new Result("DoublyLinkedListWithTail", "pushBack", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static java.util.List<Result> benchmarkPopBack() {
        System.out.println("⏱️  PopBack (esperado: O(n) vs O(1) con tail y doubly)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            // SinglyLinkedList es O(n²), solo medir hasta 100
            if (n <= 1000) {
                long time1 = benchmarkPopBackImpl(SinglyLinkedList::new, n);
                results.add(new Result("SinglyLinkedList", "popBack", n, time1));
            } else {
                System.out.print("⊘");  // Indicador de que se saltó
            }
            System.out.print(".");

            long time2 = benchmarkPopBackImpl(SinglyLinkedListWithTail::new, n);
            results.add(new Result("SinglyLinkedListWithTail", "popBack", n, time2));
            System.out.print(".");

            long time3 = benchmarkPopBackImpl(DoublyLinkedList::new, n);
            results.add(new Result("DoublyLinkedList", "popBack", n, time3));
            System.out.print(".");

            long time4 = benchmarkPopBackImpl(DoublyLinkedListWithTail::new, n);
            results.add(new Result("DoublyLinkedListWithTail", "popBack", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static long benchmarkPopBackImpl(ListFactory factory, int n) {
        long avgNs = 0;
        int warmup = (n > 10_000) ? 1 : WARMUP;
        int iterations = (n > 10_000) ? 2 : ITERATIONS;

        for (int w = 0; w < warmup; w++) {
            List<Integer> list = factory.create();
            for (int i = 0; i < n; i++) list.pushBack(i);
            for (int i = 0; i < n; i++) list.popBack();
        }

        for (int iter = 0; iter < iterations; iter++) {
            List<Integer> list = factory.create();
            for (int i = 0; i < n; i++) list.pushBack(i);

            long start = System.nanoTime();
            for (int i = 0; i < n; i++) list.popBack();
            avgNs += (System.nanoTime() - start);
        }
        return avgNs / iterations;
    }

    static java.util.List<Result> benchmarkFind() {
        System.out.println("⏱️  Find (esperado: O(n))");
        java.util.List<Result> results = new ArrayList<>();
        Random rand = new Random(42);

        for (int n : SIZES) {
            List<Integer> sll = new SinglyLinkedList<>();
            for (int i = 0; i < n; i++) sll.pushBack(i);
            long time1 = measureAvg(() -> {
                for (int i = 0; i < 10; i++) sll.find(rand.nextInt(n));
            }, n);
            results.add(new Result("SinglyLinkedList", "find", n, time1));
            System.out.print(".");

            List<Integer> sllwt = new SinglyLinkedListWithTail<>();
            for (int i = 0; i < n; i++) sllwt.pushBack(i);
            long time2 = measureAvg(() -> {
                for (int i = 0; i < 10; i++) sllwt.find(rand.nextInt(n));
            }, n);
            results.add(new Result("SinglyLinkedListWithTail", "find", n, time2));
            System.out.print(".");

            List<Integer> dll = new DoublyLinkedList<>();
            for (int i = 0; i < n; i++) dll.pushBack(i);
            long time3 = measureAvg(() -> {
                for (int i = 0; i < 10; i++) dll.find(rand.nextInt(n));
            }, n);
            results.add(new Result("DoublyLinkedList", "find", n, time3));
            System.out.print(".");

            List<Integer> dllwt = new DoublyLinkedListWithTail<>();
            for (int i = 0; i < n; i++) dllwt.pushBack(i);
            long time4 = measureAvg(() -> {
                for (int i = 0; i < 10; i++) dllwt.find(rand.nextInt(n));
            }, n);
            results.add(new Result("DoublyLinkedListWithTail", "find", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static java.util.List<Result> benchmarkAddBefore() {
        System.out.println("⏱️  AddBefore (esperado: O(n) vs O(1) con doubly)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            long time1 = benchmarkAddBeforeImpl(SinglyLinkedList::new, n);
            results.add(new Result("SinglyLinkedList", "addBefore", n, time1));
            System.out.print(".");

            long time2 = benchmarkAddBeforeImpl(SinglyLinkedListWithTail::new, n);
            results.add(new Result("SinglyLinkedListWithTail", "addBefore", n, time2));
            System.out.print(".");

            long time3 = benchmarkAddBeforeImpl(DoublyLinkedList::new, n);
            results.add(new Result("DoublyLinkedList", "addBefore", n, time3));
            System.out.print(".");

            long time4 = benchmarkAddBeforeImpl(DoublyLinkedListWithTail::new, n);
            results.add(new Result("DoublyLinkedListWithTail", "addBefore", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static long benchmarkAddBeforeImpl(ListFactory factory, int n) {
        long avgNs = 0;
        int warmup = (n > 10_000) ? 1 : WARMUP;
        int iterations = (n > 10_000) ? 2 : ITERATIONS;

        for (int w = 0; w < warmup; w++) {
            List<Integer> list = factory.create();
            Position<Integer> pos = list.pushBack(0);
            for (int i = 1; i < n; i++) list.pushBack(i);
            for (int i = 0; i < Math.min(100, n); i++) {
                list.addBefore(pos, -1);
            }
        }

        for (int iter = 0; iter < iterations; iter++) {
            List<Integer> list = factory.create();
            Position<Integer> pos = list.pushBack(0);
            for (int i = 1; i < n; i++) list.pushBack(i);

            long start = System.nanoTime();
            for (int i = 0; i < Math.min(100, n); i++) {
                list.addBefore(pos, -1);
            }
            avgNs += (System.nanoTime() - start);
        }
        return avgNs / iterations;
    }

    static java.util.List<Result> benchmarkAddAfter() {
        System.out.println("⏱️  AddAfter (esperado: O(1) para all)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            long time1 = benchmarkAddAfterImpl(SinglyLinkedList::new, n);
            results.add(new Result("SinglyLinkedList", "addAfter", n, time1));
            System.out.print(".");

            long time2 = benchmarkAddAfterImpl(SinglyLinkedListWithTail::new, n);
            results.add(new Result("SinglyLinkedListWithTail", "addAfter", n, time2));
            System.out.print(".");

            long time3 = benchmarkAddAfterImpl(DoublyLinkedList::new, n);
            results.add(new Result("DoublyLinkedList", "addAfter", n, time3));
            System.out.print(".");

            long time4 = benchmarkAddAfterImpl(DoublyLinkedListWithTail::new, n);
            results.add(new Result("DoublyLinkedListWithTail", "addAfter", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static long benchmarkAddAfterImpl(ListFactory factory, int n) {
        long avgNs = 0;
        int warmup = (n > 10_000) ? 1 : WARMUP;
        int iterations = (n > 10_000) ? 2 : ITERATIONS;

        for (int w = 0; w < warmup; w++) {
            List<Integer> list = factory.create();
            Position<Integer> pos = list.pushBack(0);
            for (int i = 1; i < n; i++) list.pushBack(i);
            for (int i = 0; i < Math.min(100, n); i++) {
                list.addAfter(pos, -1);
            }
        }

        for (int iter = 0; iter < iterations; iter++) {
            List<Integer> list = factory.create();
            Position<Integer> pos = list.pushBack(0);
            for (int i = 1; i < n; i++) list.pushBack(i);

            long start = System.nanoTime();
            for (int i = 0; i < Math.min(100, n); i++) {
                list.addAfter(pos, -1);
            }
            avgNs += (System.nanoTime() - start);
        }
        return avgNs / iterations;
    }

    static java.util.List<Result> benchmarkErase() {
        System.out.println("⏱️  Erase (esperado: O(n) vs O(1) con doubly)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            long time1 = benchmarkEraseImpl(SinglyLinkedList::new, n);
            results.add(new Result("SinglyLinkedList", "erase", n, time1));
            System.out.print(".");

            long time2 = benchmarkEraseImpl(SinglyLinkedListWithTail::new, n);
            results.add(new Result("SinglyLinkedListWithTail", "erase", n, time2));
            System.out.print(".");

            long time3 = benchmarkEraseImpl(DoublyLinkedList::new, n);
            results.add(new Result("DoublyLinkedList", "erase", n, time3));
            System.out.print(".");

            long time4 = benchmarkEraseImpl(DoublyLinkedListWithTail::new, n);
            results.add(new Result("DoublyLinkedListWithTail", "erase", n, time4));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static long benchmarkEraseImpl(ListFactory factory, int n) {
        long avgNs = 0;
        int warmup = (n > 10_000) ? 1 : WARMUP;
        int iterations = (n > 10_000) ? 2 : ITERATIONS;

        for (int w = 0; w < warmup; w++) {
            List<Integer> list = factory.create();
            java.util.List<Position<Integer>> positions = new ArrayList<>();
            for (int i = 0; i < n; i++) positions.add(list.pushBack(i));
            for (int i = 0; i < Math.min(100, n); i++) {
                list.erase(positions.get(i));
            }
        }

        for (int iter = 0; iter < iterations; iter++) {
            List<Integer> list = factory.create();
            java.util.List<Position<Integer>> positions = new ArrayList<>();
            for (int i = 0; i < n; i++) positions.add(list.pushBack(i));

            long start = System.nanoTime();
            for (int i = 0; i < Math.min(100, n); i++) {
                list.erase(positions.get(i));
            }
            avgNs += (System.nanoTime() - start);
        }
        return avgNs / iterations;
    }

    static java.util.List<Result> benchmarkStackPush() {
        System.out.println("⏱️  Stack.push() (esperado: O(1) amortizado)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            MyStack<Integer> stack = new MyStack<>();
            long time = measureAvg(() -> {
                for (int i = 0; i < n; i++) stack.push(i);
            }, n);
            results.add(new Result("MyStack", "push", n, time));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static java.util.List<Result> benchmarkStackPop() {
        System.out.println("⏱️  Stack.pop() (esperado: O(1))");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            long time = 0;
            int warmup = (n > 10_000) ? 1 : WARMUP;
            int iterations = (n > 10_000) ? 2 : ITERATIONS;

            for (int w = 0; w < warmup; w++) {
                MyStack<Integer> stack = new MyStack<>();
                for (int i = 0; i < n; i++) stack.push(i);
                for (int i = 0; i < n; i++) stack.pop();
            }

            for (int iter = 0; iter < iterations; iter++) {
                MyStack<Integer> stack = new MyStack<>();
                for (int i = 0; i < n; i++) stack.push(i);

                long start = System.nanoTime();
                for (int i = 0; i < n; i++) stack.pop();
                time += (System.nanoTime() - start);
            }
            results.add(new Result("MyStack", "pop", n, time / iterations));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static java.util.List<Result> benchmarkQueueEnqueue() {
        System.out.println("⏱️  Queue.enqueue() (esperado: O(1) amortizado)");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            MyQueue<Integer> queue = new MyQueue<>();
            long time = measureAvg(() -> {
                for (int i = 0; i < n; i++) queue.enqueue(i);
            }, n);
            results.add(new Result("MyQueue", "enqueue", n, time));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    static java.util.List<Result> benchmarkQueueDequeue() {
        System.out.println("⏱️  Queue.dequeue() (esperado: O(1))");
        java.util.List<Result> results = new ArrayList<>();

        for (int n : SIZES) {
            long time = 0;
            int warmup = (n > 10_000) ? 1 : WARMUP;
            int iterations = (n > 10_000) ? 2 : ITERATIONS;

            for (int w = 0; w < warmup; w++) {
                MyQueue<Integer> queue = new MyQueue<>();
                for (int i = 0; i < n; i++) queue.enqueue(i);
                for (int i = 0; i < n; i++) queue.dequeue();
            }

            for (int iter = 0; iter < iterations; iter++) {
                MyQueue<Integer> queue = new MyQueue<>();
                for (int i = 0; i < n; i++) queue.enqueue(i);

                long start = System.nanoTime();
                for (int i = 0; i < n; i++) queue.dequeue();
                time += (System.nanoTime() - start);
            }
            results.add(new Result("MyQueue", "dequeue", n, time / iterations));
            System.out.print(".");
        }
        System.out.println(" ✓");
        return results;
    }

    @FunctionalInterface
    interface ListFactory {
        List<Integer> create();
    }

    /**
     * Mide el tiempo promedio de una tarea con warmup y garbage collection.
     * Para n > 10_000, usa menos iteraciones para evitar tiempos excesivos.
     */
    static long measureAvg(Runnable task, int n) {
        int warmup = (n > 10_000) ? 1 : WARMUP;
        int iterations = (n > 10_000) ? 2 : ITERATIONS;

        // Warmup
        for (int i = 0; i < warmup; i++) task.run();

        // Mediciones reales
        long totalNs = 0;
        for (int i = 0; i < iterations; i++) {
            System.gc();
            long start = System.nanoTime();
            task.run();
            totalNs += (System.nanoTime() - start);
        }
        return totalNs / iterations;
    }

    static void saveResultsToCSV(String filename, java.util.List<Result> results) throws Exception {
        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.println("Implementación,Método,n,TiempoNs,TiempoUs");
            results.forEach(pw::println);
        }
        System.out.println("✅ Guardado: " + filename + " (" + results.size() + " mediciones)");
    }
}
