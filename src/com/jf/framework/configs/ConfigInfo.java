package com.jf.framework.configs;

/**
 * 通用配置管理存取接口
 * @package	com.jf.framework.configs
 * @author	Jr
 * @time	2016年11月2日
 */
public interface ConfigInfo {
	/**
	 * 获取配置 key对应的value值
	 * @param key
	 * @param nameSpace		配置文件命名空间
	 * @return <T> 成功返回Integer、String、Double	失败返回null
	 */
	<T> T getProperty(String key, String... namingSpace);
	
	/**
	 * 把value值存到对应命名空间中的key中
	 * @param key
	 * @param val
	 * @param namingSpace	配置文件命名空间
	 */
	void putProperty(String key, String val, String namingSpace);
	
	/**
	 * 把value值存到默认命名空间中的key中
	 * @param key
	 * @param val
	 */
	void putProperty(String key, String val);
}
