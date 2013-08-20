package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.HashMap;

public class CacheBase implements Serializable {
	
	private static final long serialVersionUID = 1L;

	class CacheItem implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private long insertTime;
		private Object cacheObject;
		
		public CacheItem(long insertTime, Object cacheObject) {
			super();
			this.insertTime = insertTime;
			this.cacheObject = cacheObject;
		}

		public long getInsertTime() {
			return insertTime;
		}

		public void setInsertTime(long insertTime) {
			this.insertTime = insertTime;
		}

		public Object getCacheObject() {
			return cacheObject;
		}

		public void setCacheObject(Object cacheObject) {
			this.cacheObject = cacheObject;
		}
	}
	
	private HashMap<Integer, CacheItem> cacheMap = new HashMap<Integer, CacheItem>();
	
	public Object get(int objectId) {
		/**
		 * Cache mekanizması yeniden kurgulanacak. Sonra devreye alıncak
		 * 20.08.2013
		 * @author serkan taş
		 */
		/*
		CacheItem cacheItem = cacheMap.get(objectId);
		
		if(cacheItem != null && (System.currentTimeMillis() - cacheItem.getInsertTime()) < ConstantDefinitions.CACHE_TIMEOUT_VALUE) {
			return cacheItem.getCacheObject();
		}
		*/
		return null;
	}
	
	public void put(int objectId, Object obj) {
		cacheMap.clear();
		CacheItem cacheItem = new CacheItem(System.currentTimeMillis(), obj);
		cacheMap.put(objectId, cacheItem);
	}
}
