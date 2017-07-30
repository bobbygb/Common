package com.tea.common.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tea.common.spring.DefaultTransactionSynchronization;

@Service
public class RedisLockImp implements IRedisLock {

	static Log log = LogFactory.getLog(RedisLockImp.class);
	private static final String PREX = "RedisLock->";

	@Autowired
	private IRedisService redis;
	
	private class TransactionSynchronizationEx extends DefaultTransactionSynchronization
	{
		String lockName;
		TransactionSynchronizationEx(String name)
		{
			this.lockName = name;
		}
		@Override
		public void afterCompletion(int status) {
			unlock(lockName);
		}
	}

	private ThreadLocal<List<String>> localThreadlockName = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<>();
		}
	};

	/**
	 * @category 根据名称锁
	 * @param name
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public void lock(String name) {
		lockWithManualUnlock(name);
	}

	
	/**
	 * @category 内存锁
	 * @param name
	 * @param timeout 单位上秒
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public void lock(String name, int timeout) {
		lockWithManualUnlock(name,timeout);
	}
	
	
	@Override
	public void unlock(String name) {
		localThreadlockName.get().remove(name);
		log.debug("unlock -> " + name);
		redis.del(PREX + name);
	}

	@Override
	public boolean repeatOperateLock(String name, int timeout) {
		boolean f = redis.incr(PREX + name, 1L) == 1L;
		if(f)
		{
			redis.expire(PREX + name, timeout);
		}
		return f;
	}

	@Override
	public void repeatOperateLockWithException(String name, int timeout) throws RepeatOperateExcetion {
		
		boolean f = repeatOperateLock(name,timeout);
		if(!f)
		{
			throw new RepeatOperateExcetion();
		}
	}

	/**
	 * @category 根据名称锁,这个必须手动解锁
	 * @param name
	 */
	public void lockWithManualUnlock(String name)
	{
		lockWithManualUnlock(name, 60);
	}

	/**
	 * @category 内存锁，这个必须手动解锁
	 * @param name
	 * @param timeout 单位上秒
	 */
	public void lockWithManualUnlock(String name,int timeout)
	{
		if(localThreadlockName.get().contains(name))
		{
			return;
		}
		log.debug("Try to lock -> " + name);
		if(TransactionSynchronizationManager.isActualTransactionActive())
		{
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationEx(name));
		}
		while(redis.incr(PREX + name, 1L) != 1L)
		{
			try
			{
				Thread.sleep(20);
			}catch(Exception e){}
		}
		redis.expire(PREX + name, timeout);
		log.debug("lock -> " + name);
		localThreadlockName.get().add(name);
	}
}
