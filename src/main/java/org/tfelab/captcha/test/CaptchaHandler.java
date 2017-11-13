/**
 *
 */
package org.tfelab.captcha.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tfelab.common.Configs;

import java.io.InputStream;

/**
 * @author Dectinc
 * @version Oct 2, 2014 10:37:44 PM
 */
public class CaptchaHandler {

	private static final Logger logger = LogManager.getLogger(CaptchaHandler.class.getSimpleName());
	private static String key = "";

	static {
		key = Configs.getConfig(CaptchaHandler.class).getString("capchaKey");
	}

	public static String bypass(InputStream verifyInputStream) {
		ApiResult ret = BypassCaptchaApi.Submit(key, verifyInputStream);
		if (!ret.IsCallOk) {
			logger.error("Error parsing captcha: " + ret.Error);
		}

		String value = ret.DecodedValue;
		logger.debug("Decoded captcha is: " + value);
		return value;
	}

	public static String bypass(String verifyPath) {
		ApiResult ret = BypassCaptchaApi.Submit(key, verifyPath);
		if (!ret.IsCallOk) {
			logger.error("Error parsing captcha: " + ret.Error);
		}

		String value = ret.DecodedValue;
		logger.debug("Decoded captcha is: " + value);
		return value;
	}

	public static int getLeftCredits() {
		ApiResult ret = BypassCaptchaApi.GetLeft(key);
		if (!ret.IsCallOk) {
			System.out.println("Error: " + ret.Error);
			return -1;
		}
		logger.info("Captcha credits left on this key: " + ret.LeftCredits);
		return Integer.parseInt(ret.LeftCredits);
	}

}
