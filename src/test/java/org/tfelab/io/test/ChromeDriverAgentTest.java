package org.tfelab.io.test;

import org.junit.Test;
import org.tfelab.io.requester.Task;
import org.tfelab.io.requester.chrome.ChromeDriverAgent;
import org.tfelab.io.requester.proxy.ProxyWrapper;
import org.tfelab.io.requester.proxy.ProxyWrapperImpl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by karajan on 2017/6/3.
 */
public class ChromeDriverAgentTest {
	@Test
	public void multiTest() throws Exception {

//		Authenticator.setDefault(new ProxyAuthenticator("tfelab", "TfeLAB2@15"));

		ChromeDriverAgent agent = new ChromeDriverAgent();

		int threadCount = 1;
		int jobCountPerThread = 100;

		long t = System.currentTimeMillis();

		CountDownLatch counter = new CountDownLatch(threadCount * jobCountPerThread);

		for(int i=0; i<threadCount; i++) {

			Thread thread = new Thread(() -> {

				String cookie = "thw=sg; _l_g_=Ug%3D%3D; lgc=%5Cu65F6%5Cu95F4%5Cu7B80%5Cu53F2sh; cookie1=UR2LSj1FLt5wuXTDK2WesmTk%2BLb4z1S10C0R5gfUNrY%3D; cnaui=3217360329; existShop=MTQ5MTI0OTkzNA%3D%3D; cookie2=1c472f6674580c01f8595ee4ac200827; uss=UomPs8vMtlMkF%2BLwRnWk27u71IGbrZR04Jt6P7qrKRV5Zstcyq11qhhTmg%3D%3D; sg=h93; _uab_collina=149125118731191567740864; swfstore=296697; skt=ae3d0eb6b61c8f76; _tb_token_=333a3ee3e7488; hng=SG%7Czh-CN%7CSGD; _med=dw:1280&dh:1024&pw:1280&ph:1024&ist:0; uc3=sg2; _umdata=55F3A8BFC9C50DDA9FA3F99A8D8091EAECBCD63A41EE91D53293C656CE0755DFD5A31555FBA3B787CD43AD3E795C914C1A82BC311F21582F7CBD20890DEB2F2D; tracknick=%5Cu65F6%5Cu95F4%5Cu7B80%5Cu53F2sh; unb=3217360329; _cc_=W5iHLLyFfA%3D%3D; CNZZDATA30058279=cnzz_eid%3D1397311091-1491250149-%26ntime%3D1491247788; cookie17=UNJQ6sAKYk%2FkEA%3D%3D; UM_distinctid=15b357a64d10-0cd5a4d72018b3-6510157a-1fa400-15b357a64d2724; _nk_=%5Cu65F6%5Cu95F4%5Cu7B80%5Cu53F2sh; tg=5; t=e1e6dbf52c2a81dcb7118622fcf29fa9; v=0; x=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; cna=h6FpEakqADsCAXL6Yddp4Ozb; CNZZDATA1252911424=1815120970-1491249424-%7C1491304156; uc1=cookie15=Vq8l%2BKCLz3%2F65A%3D%3D&cookie14=UoW%2Bufuv2FCk8Q%3D%3D; mt=ci=0_1; l=Avn5ldZGnM/0H/330UQ9s58SiW/TBu24; isg=An9_Ag7pG1VNqB9JkWgWBGdmDlMCiNMGETONYBFMGy51IJ-iGTRjVv0yFEcl ";

				for(int j=0; j<jobCountPerThread; j++) {

					System.err.println("Iteration: " + j);

					Task t1;

					try {
						/**
						 * TODO
						 * 不支持https连接的代理切换
						 */
						if(true){
							t1 = new Task("https://2.taobao.com/item.htm?id=547557799327", null, cookie, null);

							ProxyWrapper pw = new ProxyWrapperImpl("tetra-data.com", 60101, "tfelab", "TfeLAB2@15");
							t1.setProxyWrapper(pw);
						}

						t1.setPre_proc(true);

						agent.fetch(t1, 30000);

						if(t1.getException() != null){
							t1.getException().printStackTrace();
						}
						System.err.println(t1.getResponse().getText().contains("\"登录页面\"改进建议"));
						cookie = t1.getResponse().getCookies();

						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						//System.err.println(t.response.findElement("asdfcdsd"));

					} catch (MalformedURLException | URISyntaxException e) {
						e.printStackTrace();
					}

					counter.countDown();
				}
			});

			thread.start();
		}

		try {
			counter.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		agent.close();
	}

}