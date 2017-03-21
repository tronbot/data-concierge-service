package io.tronbot.dc.helper

import org.apache.commons.lang3.StringUtils

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 28, 2017
 */
class StringHelper {
	/**
	 *
	 * @param keywords
	 * @return keywords without 'null', multi whitespace, multi tab, multi backslash.
	 */
	public static String groomKeywords(String keywords){
		if(!keywords){
			return ''
		}
		return keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').replace(', ', ',').trim().replaceAll(' +', ' ')
	}



	public static String stripPhoneNumber(String phoneNumber){
		if(!phoneNumber){
			return null;
		}
		return StringUtils.replaceAll(phoneNumber, '[\\D]','').substring(0,10)
	}
}
