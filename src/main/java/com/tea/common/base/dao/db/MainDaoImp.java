package com.tea.common.base.dao.db;

import java.io.IOException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tea.common.base.dao.BaseDaoImp;
import com.tea.common.base.dao.SqlRowSetEx;
import com.tea.common.base.dao.TimeoutException;
import com.tea.common.base.dao.WaitForConnect;
import com.tea.common.common.ITransactionNoAnnotation;

@Repository
public class MainDaoImp extends BaseDaoImp implements MainDao {

    @Autowired
    private ITransactionNoAnnotation iTransactionNoAnnotation;
    
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public long genSeq(String seqName) {
	    
	    try
	    {
	        return iTransactionNoAnnotation.execute4Return(()->{
	            final String sql = "select * from sequence where name = ? for update";
	            final String updateSql = "update sequence set current_value = current_value+increment where name = ?";
	            SqlRowSetEx sr =  queryForRowSet(sql, seqName);
	            if(sr.next())
	            {
	                long value = sr.getLong("current_value");
	                int k = update(updateSql, seqName);
	                if(k != 1)
	                {
	                    throw new IOException("update sequence error");
	                }
	                return value;  
	            }else
	            {
	                throw new IOException(seqName + " not found");
	            }

	        });
	    }catch(Exception e)
	    {
	        throw new RuntimeException(e);
	    }
	    
		//return this.queryForFirstValueLong("select nextval(?)", seqName);
	}

	@Override
	public void waitForConnectExit(WaitForConnect wait) {
		//5分钟
		waitForConnectExit(wait,5 * 60 * 1000);
	}

	@Override
	public void waitForConnectExit(WaitForConnect wait, int timeout) {
		if(timeout <= 0)
		{
			timeout = 120 * 1000;//120秒
		}
		long reqtimeout = timeout + 5000;
		long currentime = 0;
		final String sql = "SELECT 1;";
		long l = 0;

		while(!wait.isFinished())
		{
			if(l >= 2000)
			{
				this.queryForFirstValueInt(sql);
				l = 0;//2秒内重新连接
			}
			if(currentime > reqtimeout)
			{
				throw new TimeoutException();
			}
			l++;
			currentime++;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			
			
		}
		
	}

	@Resource(name = "dataSource")
	@Override
	public void setDs(DataSource ds) {
		super.setDataSource(ds);
		
	}

}
