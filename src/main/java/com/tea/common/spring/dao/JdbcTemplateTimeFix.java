package com.tea.common.spring.dao;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;

/**
 * Created by MegaX on 2017/1/4.
 */
public class JdbcTemplateTimeFix extends JdbcTemplate {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public JdbcTemplateTimeFix() {
        super();
    }

    public JdbcTemplateTimeFix(DataSource dataSource) {
        super(dataSource);
    }
    public JdbcTemplateTimeFix(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }
    protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) {
        if(args != null)
        {
            Object[] tmp = new Object[args.length];
            for(int i = 0; i < args.length;i++)
            {
                Object o = args[i];
                if(o != null && o instanceof java.util.Date)
                {
                    String dataformat = sdf.format(o);
//                    System.out.println(dataformat);
                    tmp[i] = dataformat;
                }else
                {
                    tmp[i] = o;
                }
            }
            return new ArgumentPreparedStatementSetter(tmp);
        }else
        {
            return new ArgumentPreparedStatementSetter(args);
        }
    }
}
