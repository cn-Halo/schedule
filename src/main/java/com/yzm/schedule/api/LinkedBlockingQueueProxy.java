package com.yzm.schedule.api;

import com.yzm.schedule.persistence.DelayTaskPersistor;
import com.yzm.schedule.persistence.JdbcTemplate;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/11/27.
 *
 * @author yzm
 */
public class LinkedBlockingQueueProxy implements BlockingQueue {

    private LinkedBlockingQueue queue;
    private DelayTaskPersistor delayTaskPersistor;

    public LinkedBlockingQueueProxy() {
        queue = new LinkedBlockingQueue();
    }

    public LinkedBlockingQueueProxy(ScheduleExecutor executor, String executorName, JdbcTemplate jdbcTemplate) {
        this();
        delayTaskPersistor = new DelayTaskPersistor(executor, executorName, jdbcTemplate);
    }

    public DelayTaskPersistor getDelayTaskPersistor() {
        return this.delayTaskPersistor;
    }

    @Override
    public boolean add(Object o) {
        if (delayTaskPersistor != null) delayTaskPersistor.add(o);
        return queue.add(o);
    }

    @Override
    public boolean offer(Object o) {
        return queue.offer(o);
    }

    @Override
    public Object remove() {
        return queue.remove();
    }

    @Override
    public Object poll() {
        return queue.poll();
    }

    @Override
    public Object element() {
        return queue.element();
    }

    @Override
    public Object peek() {
        return queue.peek();
    }

    @Override
    public void put(Object o) throws InterruptedException {
        queue.put(o);
    }

    @Override
    public boolean offer(Object o, long timeout, TimeUnit unit) throws InterruptedException {
        return queue.offer(o, timeout, unit);
    }

    @Override
    public Object take() throws InterruptedException {
        Object o = queue.take();
        if (delayTaskPersistor != null) delayTaskPersistor.remove(o);
        return o;
    }

    @Override
    public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        if (delayTaskPersistor != null) delayTaskPersistor.remove(o);
        return queue.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return queue.addAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean retainAll(Collection c) {
        return queue.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return queue.containsAll(c);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return queue.toArray(a);
    }

    @Override
    public int drainTo(Collection c) {
        return queue.drainTo(c);
    }

    @Override
    public int drainTo(Collection c, int maxElements) {
        return queue.drainTo(c, maxElements);
    }
}
