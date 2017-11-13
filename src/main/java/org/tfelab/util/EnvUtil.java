package org.tfelab.util;

/**
 * 主机OS检查工具类
 * @author scisaga@gmail.com
 * @date 2017.11.12
 */
public class EnvUtil {
	
	public static boolean isHostLinux() {
		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().startsWith("linux")) {
			return true;
		}
		return false;
	}
}
