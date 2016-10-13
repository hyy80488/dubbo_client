package com.core.util;

/*
 *  数码易知 CCSE -- http://www.ccsit.cn
 *  Copyright (C)  CCSE
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.codec.binary.Base64;

import com.core.exception.CcseException;
import com.core.util.Constants;

/**
 * 字符转换类
 * 
 * @author admin
 * 
 */
public class StringUtil {
	/**
	 * 判断某个字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		if (str == null)
			return "";
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
	 * 简单的字符串格式化，性能较好。支持不多于10个占位符，从%1开始计算，数目可变。参数类型可以是字符串、Integer、Object，
	 * 甚至int等基本类型
	 * 、以及null，但只是简单的调用toString()，较复杂的情况用String.format()。每个参数可以在表达式出现多次。
	 * 
	 * @param msgWithFormat
	 * @param autoQuote
	 * @param args
	 * @return
	 */
	public static StringBuilder formatMsg(CharSequence msgWithFormat, boolean autoQuote, Object... args) {
		int argsLen = args.length;
		boolean markFound = false;

		StringBuilder sb = new StringBuilder(msgWithFormat);

		if (argsLen > 0) {
			for (int i = 0; i < argsLen; i++) {
				String flag = "%" + (i + 1);
				int idx = sb.indexOf(flag);
				// 支持多次出现、替换的代码
				while (idx >= 0) {
					markFound = true;
					sb.replace(idx, idx + 2, toString(args[i], autoQuote));
					idx = sb.indexOf(flag);
				}
			}

			if (args[argsLen - 1] instanceof Throwable) {
				StringWriter sw = new StringWriter();
				((Throwable) args[argsLen - 1]).printStackTrace(new PrintWriter(sw));
				sb.append("\n").append(sw.toString());
			} else if (argsLen == 1 && !markFound) {
				sb.append(args[argsLen - 1].toString());
			}
		}
		return sb;
	}

	public static StringBuilder formatMsg(String msgWithFormat, Object... args) {
		return formatMsg(new StringBuilder(msgWithFormat), true, args);
	}

	public static String toString(Object obj, boolean autoQuote) {
		StringBuilder sb = new StringBuilder();
		if (obj == null) {
			sb.append("NULL");
		} else {
			if (obj instanceof Object[]) {
				for (int i = 0; i < ((Object[]) obj).length; i++) {
					sb.append(((Object[]) obj)[i]).append(", ");
				}
				if (sb.length() > 0) {
					sb.delete(sb.length() - 2, sb.length());
				}
			} else {
				sb.append(obj.toString());
			}
		}
		if (autoQuote && sb.length() > 0 && !((sb.charAt(0) == '[' && sb.charAt(sb.length() - 1) == ']') || (sb.charAt(0) == '{' && sb.charAt(sb.length() - 1) == '}'))) {
			sb.insert(0, "[").append("]");
		}
		return sb.toString();
	}

	/**
	 * 把字符串中的带‘与"转成\'与\"
	 * 
	 * @param orgStr
	 * @return
	 */
	public static String convertQuot(String orgStr) {
		return orgStr.replace("'", "\\'").replace("\"", "\\\"");
	}

	public static synchronized String encryptSha256(String inputStr) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte digest[] = md.digest(inputStr.getBytes("UTF-8"));

			return new String(Base64.encodeBase64(digest));

			// return (new BASE64Encoder()).encode(digest);
			// return new String(Hex.encodeHex(digest));
		} catch (Exception e) {
			return null;
		}
	}

	public static String iso2xxx(String str, String charSet) {
		String value = "";
		if (str == null || str.length() == 0) {
			return "";
		}
		try {
			value = new String(str.getBytes("ISO8859_1"), charSet);
		} catch (Exception e) {
			return null;
		}
		return value;
	}

	/**
	 * HTML实体编码转成普通的编码
	 * 
	 * @param dataStr
	 * @return
	 */
	public static String htmlEntityToString(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			int system = 10;// 进制
			if (start == 0) {
				int t = dataStr.indexOf("&#");
				if (start != t)
					start = t;
			}
			end = dataStr.indexOf(";", start + 2);
			String charStr = "";
			if (end != -1) {
				charStr = dataStr.substring(start + 2, end);
				// 判断进制
				char s = charStr.charAt(0);
				if (s == 'x' || s == 'X') {
					system = 16;
					charStr = charStr.substring(1);
				}
			}
			// 转换
			try {
				char letter = (char) Integer.parseInt(charStr, system);
				buffer.append(new Character(letter).toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			// 处理当前unicode字符到下一个unicode字符之间的非unicode字符
			start = dataStr.indexOf("&#", end);
			if (start - end > 1) {
				buffer.append(dataStr.substring(end + 1, start));
			}

			// 处理最后面的非unicode字符
			if (start == -1) {
				int length = dataStr.length();
				if (end + 1 != length) {
					buffer.append(dataStr.substring(end + 1, length));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 把String转成html实体字符
	 * 
	 * @param str
	 * @return
	 */
	public static String stringToHtmlEntity(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			switch (c) {
			case 0x0A:
				sb.append(c);
				break;

			case '<':
				sb.append("&lt;");
				break;

			case '>':
				sb.append("&gt;");
				break;

			case '&':
				sb.append("&amp;");
				break;

			case '\'':
				sb.append("&apos;");
				break;

			case '"':
				sb.append("&quot;");
				break;

			default:
				if ((c < ' ') || (c > 0x7E)) {
					sb.append("&#x");
					sb.append(Integer.toString(c, 16));
					sb.append(';');
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 字符串 转unicode
	 * 
	 * @param s
	 * @return
	 */
	public static String stringToUnicode(String s) {
		String unicode = "";
		char[] charAry = new char[s.length()];
		for (int i = 0; i < charAry.length; i++) {
			charAry[i] = (char) s.charAt(i);
			unicode += "\\u" + Integer.toString(charAry[i], 16);
		}
		return unicode;
	}

	/**
	 * unicode转字符串
	 * 
	 * @param unicodeStr
	 * @return
	 */
	public static String unicodeToString(String unicodeStr) {
		StringBuffer sb = new StringBuffer();
		String str[] = unicodeStr.toUpperCase().split("\\\\U");
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals(""))
				continue;
			char c = (char) Integer.parseInt(str[i].trim(), 16);
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * 对比两个字符串是否相同 (注意此方法 null "" 对比是相同的) 
	 * @param str1    str1
	 * @param str2   str2
	 * @return  true 不同 false 相同
	 * @author		wangyuehui
	 * @date		Jul 31, 2014 4:00:03 PM
	 */
	public static boolean isDifferent(String str1,String str2) {
		str1 = isNull(str1);
		str2 = isNull(str2);
		return !str1.equals(str2);
	}
	

	/**
	 * 转换html
	 * 
	 * @param inputString
	 * @return
	 */
	public static String html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script>]*?>[\s\S]*?<\/script>
			// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style>]*?>[\s\S]*?<\/style>
			// }
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_script = java.util.regex.Pattern.compile(regEx_script, java.util.regex.Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = java.util.regex.Pattern.compile(regEx_style, java.util.regex.Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = java.util.regex.Pattern.compile(regEx_html, java.util.regex.Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}

	/**
	 * 获取8位随机字符串
	 * 
	 * @return
	 */
//	public static String get8rands() {
//		Random rd = new Random(); // 創建隨機對象
//		String n = ""; // 保存隨機數
//		int rdGet; // 取得隨機數
//		do {
//			if (rd.nextInt() % 2 == 1) {
//				rdGet = Math.abs(rd.nextInt()) % 10 + 48; // 產生48到57的隨機數(0-9的鍵位值)
//			} else {
//				rdGet = Math.abs(rd.nextInt()) % 26 + 97; // 產生97到122的隨機數(a-z的鍵位值)
//			}
//			char num1 = (char) rdGet; // int轉換char
//			String dd = Character.toString(num1);
//			n += dd;
//
//		} while (n.length() < 8);// 設定長度，此處假定長度小於8
//
//		return n;
//	}

	// 字符串是否空
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().equals(""));
	}
	
	public static String isNull(String str) {
		if (str == null || str.trim().equals("")){
			return "";
		}else{
			return str;
		}
	}

	// 字符串是否不空
	public static boolean isNotEmpty(String str) {
		return (str != null && !str.trim().equals(""));
	}
	
	// 字符串"NULL"
	public static boolean isStringNull(String str) {
		return "NULL".equals(str) ;
	}

	/**
	 * 将传入的字符串格式化：传入 a,b,c 格式化后 'a','b','c'
	 * 
	 * @param sSource
	 *            String
	 * @return String
	 */
	public static String formatStringA(String sSource) {
		if (sSource == null)
			sSource = "''";
		else if (sSource.equals(""))
			sSource = "''";
		else {
			sSource = sSource.replaceAll(",", "','");
			sSource = "'" + sSource + "'";
		}

		return sSource;
	}

	/**
	 * 分割字串
	 * 
	 * @param source
	 *            原始字符
	 * @param div
	 *            分割符
	 * @return 字符串数组
	 */
	public static final String[] split(String source, String div) {
		int arynum = 0, intIdx = 0, intIdex = 0, div_length = div.length();
		if (source.compareTo("") != 0) {
			if (source.indexOf(div) != -1) {
				intIdx = source.indexOf(div);
				for (int intCount = 1;; intCount++) {
					if (source.indexOf(div, intIdx + div_length) != -1) {
						intIdx = source.indexOf(div, intIdx + div_length);
						arynum = intCount;
					} else {
						arynum += 2;
						break;
					}
				}
			} else {
				arynum = 1;
			}
		} else {
			arynum = 0;

		}
		intIdx = 0;
		intIdex = 0;
		String[] returnStr = new String[arynum];

		if (source.compareTo("") != 0) {
			if (source.indexOf(div) != -1) {
				intIdx = (int) source.indexOf(div);
				returnStr[0] = (String) source.substring(0, intIdx);
				for (int intCount = 1;; intCount++) {
					if (source.indexOf(div, intIdx + div_length) != -1) {
						intIdex = (int) source.indexOf(div, intIdx + div_length);
						returnStr[intCount] = (String) source.substring(intIdx + div_length, intIdex);
						intIdx = (int) source.indexOf(div, intIdx + div_length);
					} else {
						returnStr[intCount] = (String) source.substring(intIdx + div_length, source.length());
						break;
					}
				}
			} else {
				returnStr[0] = (String) source.substring(0, source.length());
				return returnStr;
			}
		} else {
			return returnStr;
		}
		return returnStr;
	}
	
	//
	public static String addZero(int num, int len) throws CcseException {
		String ls = Integer.toString(num);
		if (("" + num).length() > len) {
			CcseException se = new CcseException();
			throw se;
		} else {
			for (int i = ls.length(); i < len; i++) {
				ls = "0" + ls;
			}
		}
		return ls;
	}
	
	/**
	 * 根据上一个版本号获得下一个版本号,只处理格式类似1.0的，将版本号升为2.0,3.0,4.0……
	 * @param prevVersion 前序版本号
	 * @return 后续版本号，若前序版本号为空，则返回1.0
	 * @throws CcseException
	 */
	public static String getNextVersion(String prevVersion)  throws CcseException {
		String defaultStr = "1.0";
		if(StringUtil.isEmpty(prevVersion)){
			return defaultStr;
		}else{
			String preStr = prevVersion.substring(0, prevVersion.indexOf("."));
			String supStr = prevVersion.substring(prevVersion.indexOf("."));
			int temp = Integer.parseInt(preStr)+1;
			return temp+supStr;

		}
	}
	
	
	/**
	 * 根据当前版本号，获取相对于的 两位版本号  01、02、...10...99
	 * @param prevVersion 真实版本号
	 * @return 两位版本号
	 * @throws CcseException
	 */
	public static String getTwoBitVersion(String prevVersion)  throws CcseException {
		String defaultStr = "01";
		if(StringUtil.isEmpty(prevVersion)){
			return defaultStr;
		}else{
			String preStr = prevVersion.substring(0, prevVersion.indexOf("."));
			return String.format("%"+Constants.CLIENT_VERSION+"s", preStr).replace(' ', '0');
		}
	}
	
	/**
	 * 将为null的字符串转为空字符串 将不为null的字符串trim()
	 * @param target  目标字符串
	 * @author pyyc
	 * @return 
	 */
	public static String getNotNullString(String target){
		if(target == null) {
			return "";
		} else {
			return target.trim();
		}
	}
	
	/**
	 * 把struts转换的逗号分隔的字符串中的空格去掉
	 * 例如：1, 2, 3 转成  1,2,3
	 * @param str
	 * @return
	 */
	public static String formatStrutsString(String str){
		if(str == null){
			return "";
		}else{
			StringBuffer sb = new StringBuffer();
			String[] methods = StringUtil.split(str, ", ");
			if(methods.length > 0){
				for (String string : methods) {
					if(sb.length()==0){
						sb.append(string);
					}else{
						sb.append(",").append(string);
					}
				}
			}
			return sb.toString();
		}
	}
	
	/**
	 * 把类似于0,1,2字符串转化为('0','1','2')
	 * @param tarId
	 * @return
	 */
	public static String praseHqlOfInString(String tarId){
		if(tarId==null||tarId.length()==0){
			return "";
		}
		String[] strs = StringUtil.split(tarId, ",");
		StringBuffer subBuffer = new StringBuffer();
		subBuffer.append("(");
		for(String str : strs){
			subBuffer.append("'").append(str).append("',");
		}
		if(subBuffer.length()>0){
			subBuffer.deleteCharAt(subBuffer.length()-1);
		}
		subBuffer.append(")");
		return subBuffer.toString();
	}
	
	/**
	 * 去掉字符串两端的空格
	 * @param val
	 * @return
	 */
	public static String strToTrimVal(Object val){
		if(val!=null){
			return val.toString().trim();
		}
		return null;
	}
	/**
	 * 首字母大写
	 * @param name
	 * @return
	 */
	public static String toUpperCaseFirstOne(String name) {
        char[] ch = name.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (i == 0) {
                ch[0] = Character.toUpperCase(ch[0]);
            } else {
                ch[i] = Character.toLowerCase(ch[i]);
            }
        }
        StringBuffer a = new StringBuffer();
        a.append(ch);
        return a.toString();
    }
	
	/** 
     * JSON字符串特殊字符处理，比如：“\A1;1300” 
     * @param s 
     * @return String 
     */  
	public static String stringToJson(String s) {    
        StringBuffer sb = new StringBuffer ();     
        for (int i=0; i<s.length(); i++) {     
      
            char c = s.charAt(i);     
            switch (c) {     
            case '\"':     
                sb.append("\\\"");     
                break;     
//            case '\\':   //如果不处理单引号，可以释放此段代码，若结合下面的方法处理单引号就必须注释掉该段代码
//                sb.append("\\\\");     
//                break;     
            case '/':     
                sb.append("\\/");     
                break;     
            case '\b':      //退格
                sb.append("\\b");     
                break;     
            case '\f':      //走纸换页
                sb.append("\\f");     
                break;     
            case '\n':     
                sb.append("\\n"); //换行    
                break;     
            case '\r':      //回车
                sb.append("\\r");     
                break;     
            case '\t':      //横向跳格
                sb.append("\\t");     
                break;     
            default:     
                sb.append(c);    
            }}
        return sb.toString();     
     }
	
	
	  /**  
     * 判断是否是一个中文汉字  
     *   
     * @param c  
     *            字符  
     * @return true表示是中文汉字，false表示是英文字母  
	 * @throws UnsupportedEncodingException 
     * @throws UnsupportedEncodingException  
     *             使用了JAVA不支持的编码格式  
     */  
    public static boolean isChineseChar(char c) throws UnsupportedEncodingException  {   
        // 如果字节数大于1，是汉字   
        // 以这种方式区别英文字母和中文汉字并不是十分严谨，但在这个题目中，这样判断已经足够了   
        return String.valueOf(c).getBytes("utf-8").length > 1;   
    }   
  
    /**  
     * 按字节截取字符串  
     *   
     * @param orignal  
     *            原始字符串  
     * @param count  
     *            截取位数  
     * @return 截取后的字符串  
     * @throws UnsupportedEncodingException 
     * @throws UnsupportedEncodingException  
     *             使用了JAVA不支持的编码格式  
     */  
    public static String substring(String orignal, int count) throws UnsupportedEncodingException  {   
        // 原始字符不为null，也不是空字符串   
        if (orignal != null && !"".equals(orignal)) {   
            // 将原始字符串转换为GBK编码格式   
            orignal = new String(orignal.getBytes(), "utf-8");   
            // 要截取的字节数大于0，且小于原始字符串的字节数   
            if (count > 0 && count < orignal.getBytes("utf-8").length) {   
                StringBuffer buff = new StringBuffer();   
                char c;   
               for (int i = 0; i < count; i++) {   
                   c = orignal.charAt(i);   
                    buff.append(c); 
                    
                    if (StringUtil.isChineseChar(c)) {   
                        // 遇到中文汉字，截取字节总数减1   
                        count--;
                        count--;
                    }   
                }   
                return buff.toString();   
            }   
        }   
        return orignal;   
    }   
    
    public static String ClobToString(Clob clob) {
        String reString = "";
        Reader is = null;
        try {
            is = clob.getCharacterStream();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 得到流
        BufferedReader br = new BufferedReader(is);
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        while (s != null) {
            //执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
            sb.append(s);
            try {
                s = br.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        reString = sb.toString();
        return reString;
    }

	public static void main(String[] args) {
		String orignal = "<html>dadsad</html>";
		System.out.println(StringUtil.html2Text(orignal));
	}

}
