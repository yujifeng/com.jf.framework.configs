package com.jf.framework.configs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Properties处理类
 * 支持单行key=value形式的数据
 * 支持单行注释"#"
 * 
 * TODO	支持换行
 * 
 * @package	com.jf.framework.configs
 * @author	Jr
 * @time	2016年11月2日
 */
public class Properties {
	/**
	 * 注释标识位 "#"
	 */
	private String COMMENT_CHAR = "#";
	/**
	 * key-value 分割标识位 "="
	 */
	private String SPLIT_CHAR = "=";
	
	private Map<String, String> propertiesMap = new LinkedHashMap<String, String>();
	private Log log = LogFactory.getLog(Properties.class);
	private String configPath;
	
	public Properties() {}
	public Properties(String configPath){
		this.configPath = configPath;
	}
	
	public void load(){
		load(configPath);
	}
	
	public void load(String configPath){
		this.configPath = configPath;
		FileReader fr =  null;
		try{
			fr = new FileReader(configPath);
			load(fr);
		}catch(FileNotFoundException e){
			log.error("无法读取properties文件:" + configPath);
		}finally{
			if(fr != null){
				try{
					fr.close();
				}catch(IOException e){
					log.error(e);
				}
			}
		}
	}
	
	/**
	 * 逐行读取文件内容
	 * TODO 支持换行读取内容
	 * @param isr
	 */
	public void load(InputStreamReader isr){
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(isr);
			while((line = br.readLine()) != null){
				if(line.length() == 0){
					continue;
				}
				// 过滤注释
				if(line.startsWith(COMMENT_CHAR)){
					continue;
				}
				String[] kv = line.split(SPLIT_CHAR);
				if(kv == null || kv.length < 2){
					continue;
				}
				propertiesMap.put(kv[0], kv[1]);
			}
		} catch (IOException e) {
			log.error(e);
		} finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
	
	/**
	 * 获取propertiesMap中key对应的值
	 * 支持String、Integer、Double、Long
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key){
		String val = propertiesMap.get(key);
		if(!isNum(val)){
			// 不是数字则默认返回为String
			return (T)val;
		}
		
		@SuppressWarnings("rawtypes")
		Class c = null;
		if(val.indexOf(".") > 0){
			c = Double.class;
		}else{
			// TODO 这里有个漏洞 超过最大值 但是长度没超过的情况下 会有异常
			if(val.length() > (Integer.MAX_VALUE+"").length()){
				c = Long.class;
			}else{
				c = Integer.class;
			}
		}
		return (T)parse(val, c);
	}
	
	/**
	 * 往propertiesMap中添加对应的key-value
	 * @param key
	 * @param val
	 */
	public void setProperty(String key, String val){
		propertiesMap.put(key, val);
	}
	
	/**
	 * 往propertitesMap中添加对应的key-value 还支持注释
	 * @param key
	 * @param val
	 * @param comment
	 */
	public void setProperty(String key, String val, String comment){
		setProperty(COMMENT_CHAR + comment, null);
		setProperty(key, val);
	}
	
	/**
	 * 把一个数字字符串 转换为指定的 Number类型
	 * @param numStr	数字字符串
	 * @param clz		指定的类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T parse(String numStr, Class<? extends Number> clz){
		try{
			return (T) clz.getConstructor(String.class).newInstance(numStr);
		}catch(Exception e){
			log.error(e);
		}
		return null;
	}
	
	/**
	 * 把配置信息持久化到文件中，key-value支持数字和字符串，其他类型则调用其toString()的值
	 */
	public void store(){
		List<String> pList=new ArrayList<String>();
		BufferedReader br=null;
		try
		{
			br=new BufferedReader(new FileReader(configPath));
			String line=null;
			while ((line=trimST(br.readLine()))!=null)
			{
				if(line.length()==0)
				{
					continue;
				}
				pList.add(line);
			}
		}
		catch(Exception e)
		{
			log.error(e);
		}
		finally
		{
			if(br!=null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					log.error(e);
				}
			}
		}
		
		BufferedWriter bw=null;
		try
		{
			bw=new BufferedWriter(new FileWriter(configPath));
			for (String key : propertiesMap.keySet())
			{
				for (int i = 0; i < pList.size(); i++)
				{
					String lineContent=pList.get(i);
					/*
					 * 是注释则跳过
					 */
					if(lineContent.startsWith(COMMENT_CHAR))
					{
						continue;
					}
					Pattern keyPattern=Pattern.compile("^"+key);
					Matcher keyMatcher=keyPattern.matcher(lineContent);
					Pattern contentPattern=Pattern.compile("=(\\s*)"+propertiesMap.get(key));
					Matcher contentMatcher=contentPattern.matcher(lineContent);
					
					/*
					 * 修改不相同的内容
					 */
					if(keyMatcher.find() && !contentMatcher.find())
					{
						lineContent=key+SPLIT_CHAR+propertiesMap.get(key);
						pList.set(i, lineContent);
					}
				}
			}
			for (int i = 0; i < pList.size(); i++)
			{
				String l=pList.get(i);
				if(l.startsWith(COMMENT_CHAR))
				{
					bw.newLine();
				}
				bw.write(l);
				bw.newLine();
			}
			bw.flush();
		}
		catch (IOException e)
		{
			log.error(e);
		}
		finally
		{
			if(bw!=null)
			{
				try
				{
					bw.close();
				}
				catch (IOException e)
				{
					log.error(e);
				}
			}
		}
	}
	
	private String trimST(String str)
	{
		if(str==null)
		{
			return null;
		}
		String regStrBegin="^[\\s\\t]*";
		String regStrEnd="[\\s\\t]*$";
		Matcher m=Pattern.compile(regStrBegin).matcher(str);
		if(m.find())
		{
			str= m.replaceFirst("");
		}
		
		m=Pattern.compile(regStrEnd).matcher(str);
		if(m.find())
		{
			str= m.replaceFirst("");
		}
		return str;
	}
	
	/**
	 * 判断一个字符串是否为数字
	 * @param str
	 * @return
	 */
	private boolean isNum(String str){
		if(str==null)
		{
			return false;
		}
		/*
		 * 判断是否为数字带小数点
		 */
		String regEx="^(\\-?)\\d*(\\.?(\\d+))$";
		Pattern p=Pattern.compile(regEx);
		Matcher m=p.matcher(str.trim());
		return m.find();
	}
}
