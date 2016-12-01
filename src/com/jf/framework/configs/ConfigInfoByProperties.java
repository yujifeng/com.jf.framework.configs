package com.jf.framework.configs;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * 文件形式实现ConfigInfo
 * 
 * @package	com.jf.framework.configs
 * @author	Jr
 * @time	2016年11月2日
 */
@Service
public class ConfigInfoByProperties implements ConfigInfo{
	
	private Log log = LogFactory.getLog(ConfigInfoByProperties.class);
	/**
	 * 配置文件路径，默认为根目录
	 */
	private String configPath = "/";
	private String configFileName = "configs.properties";
	public String getConfigPath(){
		return configPath;
	}
	
	public void setConfigPath(String configPath){
		this.configPath = configPath;
		loadProperties();
	}
	
	public ConfigInfoByProperties(String configPath){
		this.configPath = configPath;
		loadProperties();
	}
	
	public ConfigInfoByProperties(){
		loadProperties();
	}
	
	private static final String DEFAULT_CONFIG_FILE_NAME = "configs.properties";
	private Map<String, Properties> propertiesMap = new HashMap<String, Properties>();
	
	/**
	 * 加载配置文件
	 */
	private void loadProperties(){
		URL url = ConfigInfoByProperties.class.getClassLoader().getResource(DEFAULT_CONFIG_FILE_NAME);
		
		// 加载默认配置文件
		loadDefaultProperties(url);
		// 加载自定义配置文件
		loadCustomProperties(url);
		
	}

	/**
	 * 自定义配置文件命名正则
	 */
	private static final String CUSTOM_PROPERTIES_FILE_REGEX = "configs[\\w-]*\\.properties";
	
	/**
	 * 加载自定义配置文件
	 * 该配置文件命名格式为:
	 * configs-xxx.properties	xxx部分为自定义名称
	 * 
	 * @param url
	 */
	private void loadCustomProperties(URL url){
		String customPath = url.getPath().replace(DEFAULT_CONFIG_FILE_NAME, "").replace("%20", " ");
		File path = new File(customPath);
		File[] files = path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String file) {
				Pattern p=Pattern.compile(CUSTOM_PROPERTIES_FILE_REGEX,Pattern.CASE_INSENSITIVE);
				if(p.matcher(file).find())
				{
					return true;
				}
				return false;
			}
		});
		
		if(files == null || files.length == 0){
			return;
		}
		
		for(File file : files){
			if(!file.exists() || file.getPath().endsWith(DEFAULT_CONFIG_FILE_NAME)){
				continue;
			}
			Properties p = new Properties();
			p.load(file.getPath());
			String filePath = file.getPath();
			String namingSpace = filePath.substring(filePath.lastIndexOf(File.separator)+1);
			propertiesMap.put(namingSpace, p);
		}
		
	}
	
	/**
	 * 加载默认配置文件
	 * @param url
	 */
	private void loadDefaultProperties(URL url) {
		if(url == null){
			throw new RuntimeException("在当前的ClassLoader下找不到配置管理文件(" + DEFAULT_CONFIG_FILE_NAME+ ")");
		}
		
		Properties pros = new Properties();
		pros.load(url.getPath());
		propertiesMap.put(DEFAULT_CONFIG_FILE_NAME, pros);
		
		
//		InputStream is = null;
//		InputStreamReader isr = null;
//		try{
//			is = ConfigInfoByProperties.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
//			isr = new InputStreamReader(is);
//			Properties pros = new Properties();
//			pros.load(isr);
//			propertiesMap.put(DEFAULT_CONFIG_FILE_NAME, pros);
//		}finally{
//			if(isr != null){
//				try {
//					isr.close();
//				} catch (IOException e) {
//					log.error(e);
//				}
//			}
//			if(is != null){
//				try {
//					is.close();
//				} catch (IOException e) {
//					log.error(e);
//				}
//			}
//		}
	} 
	
	/*
	 * (non-Javadoc)
	 * @see com.jf.framework.configs.ConfigInfo#getProperty(java.lang.String, java.lang.String[])
	 */
	@Override
	public <T> T getProperty(String key, String... namingSpace) {
		T rst = null;
		if(namingSpace == null || namingSpace.length == 0){
			rst = propertiesMap.get(configFileName).getProperty(key);
		}
		for(String s : namingSpace){
			rst = propertiesMap.get(s).getProperty(key);
			if(rst != null){
				return rst;
			}
		}
		return rst;
	}

	/*
	 * (non-Javadoc)
	 * @see com.jf.framework.configs.ConfigInfo#putProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void putProperty(String key, String val, String namingSpace) {
		if(namingSpace == null || namingSpace.length() == 0){
			return;
		}
		if(propertiesMap.get(namingSpace) != null){
			propertiesMap.get(namingSpace).setProperty(key, val);
			propertiesMap.get(namingSpace).store();
		}else{
			log.error("找不到命名空间:" + namingSpace + ",保存key:" + key + " val:" + val + "失败!");
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.jf.framework.configs.ConfigInfo#putProperty(java.lang.String, java.lang.String)
	 */
	@Override
	public void putProperty(String key, String val) {
		propertiesMap.get(configFileName).setProperty(key, val);
		propertiesMap.get(configFileName).store();
	}
	
}
