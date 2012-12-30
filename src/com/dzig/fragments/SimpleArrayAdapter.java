package com.dzig.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SimpleArrayAdapter<T> extends BaseAdapter{
	private final Object mLock = new Object();
	private final List<T> objects;
	
	public SimpleArrayAdapter(){
		objects = new ArrayList<T>();
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public T getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        synchronized (mLock) {
        	objects.add(object);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) {
        synchronized (mLock) {
        	objects.addAll(collection);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T ... items) {
        synchronized (mLock) {
            Collections.addAll(objects, items);
        }
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
        	objects.add(index, object);
        }
        notifyDataSetChanged();
    }

    /**
     * Set the specified Collection as new data.
     *
     * @param collection The Collection to be used as new data.
     */
    public void set(Collection<? extends T> collection) {
        synchronized (mLock) {
        	objects.clear();
        	objects.addAll(collection);
        }
        notifyDataSetChanged();
    }
    
    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        synchronized (mLock) {
            objects.remove(object);
        }
        notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            objects.clear();
        }
        notifyDataSetChanged();
    }
    
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
	
}