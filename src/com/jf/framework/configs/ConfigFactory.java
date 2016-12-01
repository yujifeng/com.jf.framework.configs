package com.jf.framework.configs;

/**
 * 配置管理工厂类
 * 
 * @package	com.jf.framework.configs
 * @author	Jr
 * @time	2016年11月2日
 */
public class ConfigFactory {
	private static ConfigInfo configInfo;
	
	/**
	 * 获取当前配置的提供者
	 * @return	存在则返回类名 否则返回null
	 */
	public static String getConfigInfoProvider(){
		if(configInfo == null){
			return null;
		}
		return configInfo.getClass().getName();
	}
	
	/**
	 * 获取配置管理提供者，
	 * @param providerClz	配置管理实现的提供类
	 * @return
	 */
	public static ConfigInfo getInstance(Class<? extends ConfigInfo> providerClz){
		if(configInfo == null){
			if(providerClz.equals(ConfigInfoByProperties.class)){
				configInfo = new ConfigInfoByProperties();
			}
		}else{
			// TODO 实现数据库配置管理
		}
		return configInfo;
	}
	
	/**
	 * 返回默认配置管理类
	 * @return
	 */
	public static ConfigInfo getDefaultProvider(){
		if(configInfo == null){
			configInfo = ConfigFactory.getInstance(ConfigInfoByProperties.class);
		}
		return configInfo;
	}
	
	public static void main(String[] args) {
		ConfigFactory.getDefaultProvider().putProperty("a", "test");
	}
}
