package com.baidu.unbiz.multiengine.cluster.zk;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.SimpleDistributedQueue;

import com.baidu.unbiz.multiengine.cluster.utils.JSONUtil;

public abstract class ZKBlockingQueue<E> implements BlockingQueue<E> {

    @SuppressWarnings("unchecked")
    public ZKBlockingQueue(CuratorFramework curator, String queuePath) {
        this.delegate = new SimpleDistributedQueue(curator, queuePath);
        this.messageType =
                (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected SimpleDistributedQueue delegate;
    protected Class<E> messageType;

    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new RuntimeException("not impl");
    }

    @Override
    public void put(E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

    @Override
    public boolean offer(E e) {
        try {
            return delegate.offer(JSONUtil.toBytes(e));
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public boolean remove(Object o) {
        return remove() != null;
    }

    @Override
    public E remove() {
        try {
            return JSONUtil.toObject(delegate.remove(), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("not impl");
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return JSONUtil.toObject(delegate.poll(timeout, unit), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E poll() {
        try {
            return JSONUtil.toObject(delegate.poll(), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E element() {
        try {
            return JSONUtil.toObject(delegate.element(), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E peek() {
        try {
            return JSONUtil.toObject(delegate.peek(), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E take() throws InterruptedException {
        try {
            return JSONUtil.toObject(delegate.take(), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean contains(Object o) {
        throw new RuntimeException("not impl");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new RuntimeException("not impl");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("not impl");
    }

    @Override
    public boolean isEmpty() {
        throw new RuntimeException("not impl");
    }

    @Override
    public int size() {
        throw new RuntimeException("not impl");
    }

    @Override
    public int remainingCapacity() {
        throw new RuntimeException("not impl");
    }

    @Override
    public void clear() {
        throw new RuntimeException("not impl");
    }

    @Override
    public Iterator<E> iterator() {
        throw new RuntimeException("not impl");
    }

    @Override
    public Object[] toArray() {
        throw new RuntimeException("not impl");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("not impl");
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        throw new RuntimeException("not impl");
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new RuntimeException("not impl");
    }
}
