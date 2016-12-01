package com.jf.framework.configs;

import org.springframework.stereotype.Service;

/**
 * 数据库形式实现ConfigInfo
 * 
 * @package	com.jf.framework.configs
 * @author	Jr
 * @time	2016年11月2日
 */
@Service
public class ConfigInfoByDB implements ConfigInfo{

	public <T> T getProperty(String key, String... namingSpace) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putProperty(String key, String... namingSpace) {
		// TODO Auto-generated method stub
		
	}

	public void putProperty(String key, String val, String namingSpace) {
		// TODO Auto-generated method stub
		
	}

	public void putProperty(String key, String val) {
		// TODO Auto-generated method stub
		
	}
}
