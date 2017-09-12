package com.mybatis.plugin;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

import com.mybatis.plugin.exception.SQLTimeException;
import com.mybatis.plugin.utils.PluginUtils;
import com.mybatis.plugin.utils.SqlUtils;
import com.mybatis.plugin.utils.StringUtils;
import com.mybatis.plugin.utils.SystemClock;
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SqlTimeInterceptor implements Interceptor{
	   /**
     * SQL 执行最大时长，超过自动停止运行，有助于发现问题。
     */
    private long maxTime = 0;

    private boolean format = false;

    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        try {
            statement.getClass().getDeclaredField("stmt");
            statement = (Statement) SystemMetaObject.forObject(statement).getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }
        String originalSql = statement.toString();
        int index = originalSql.indexOf(':');
        String sql = originalSql;
        if (index > 0) {
            sql = originalSql.substring(index + 1, originalSql.length());
        }
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long end = SystemClock.now();
        long timing = end - start;
        String formatSql = SqlUtils.sqlFormat(sql, format);
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        System.err.println(" ID：" + ms.getId() + "\n Execute SQL：" + formatSql + "\n"+" Time：" + timing + " ms");
        if (maxTime >= 1 && timing > maxTime) {
            throw new SQLTimeException(" The SQL execution time is too large, please optimize ! ");
        }
        return result;
    }

    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    public void setProperties(Properties prop) {
        String maxTime = prop.getProperty("maxTime");
        String format = prop.getProperty("format");
        if (StringUtils.isNotEmpty(maxTime)) {
            this.maxTime = Long.parseLong(maxTime);
        }
        if (StringUtils.isNotEmpty(format)) {
            this.format = Boolean.valueOf(format);
        }
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }
}
