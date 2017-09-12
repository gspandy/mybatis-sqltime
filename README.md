# mybatis-sqltime 用于测试mybaits中的sql执行时间

快速使用

1.添加pom依赖

    <dependency>
       <groupId>com.mybatis.plugin</groupId>
        <artifactId>mybatis-sqltime</artifactId>
        <version>1.0.0</version>
     </dependency>
  
 2.添加sqlMapConfig.xml配置文件
 
 	    <plugins>
	    	<plugin interceptor="com.mybatis.plugin.SqlTimeInterceptor">
	    		<property name="maxTime" value="1000" />
	    	</plugin>
	    </plugins>
      
 3.执行sql时,会打印出日志
 
 ` ID：com.mybaits.plugin.test.UserMapper.findAllUser </br>
 ` Execute SQL： select id, username, password, birthday, sex, address from user </br>
 ` Time：17 ms </br>
