package org.tfelab.io.test;

import org.junit.Test;
import org.tfelab.io.requester.chrome.ChromeDriverRequester;
import org.tfelab.io.requester.proxy.ProxyAuthenticator;
import org.tfelab.io.requester.Task;
import org.tfelab.io.requester.proxy.ProxyWrapper;
import org.tfelab.io.requester.proxy.ProxyWrapperImpl;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by karajan on 2017/6/3.
 */
public class ChromeDriverRequesterTest {

	@Test
	public void test() {

		Authenticator.setDefault(new ProxyAuthenticator("tfelab", "tfelab"));

		int threadCount = 1;
		int jobCountPerThread = 20;

		long t = System.currentTimeMillis();

		CountDownLatch counter = new CountDownLatch(threadCount * jobCountPerThread);

		for (int i = 0; i < threadCount; i++) {

			Thread thread = new Thread() {

				public void run() {

					String cookie = "v=0; _tb_token_=e1e77e184779; UM_distinctid=15ac8ea0104110-0def5ff79ba49a-6510157a-1fa400-15ac8ea0105a1a; CNZZDATA30057895=cnzz_eid%3D1076685110-1489427105-%26ntime%3D1489427105; thw=cn; uc3=sg2=VqpfgJmLNrZT7aLlMnGl9JAKHKX9zpb7e%2FeZ8w4Dx78%3D&nk2=2W36vvP5Iwmruw%3D%3D&id2=UNGR61JedfNA7Q%3D%3D&vt3=F8dARVWNVxulBbebNh0%3D&lg2=Vq8l%2BKCLz3%2F65A%3D%3D; existShop=MTQ4OTQ0MjUxNg%3D%3D; uss=VW63OjNihz2D3SkipbcSzYMxOHJlRaGjBb7DgmiiNQerTVyirzqffB9J; lgc=%5Cu548C%5Cu70ED%5Cu7834%5Cu4EBA%5Cu751F; tracknick=%5Cu548C%5Cu70ED%5Cu7834%5Cu4EBA%5Cu751F; cookie2=1c29ec1623fc1de10ce92fb444759a79; sg=%E7%94%9F27; mt=np=&ci=0_1; cookie1=ACPloZJZy0TzgKtFKkqdjZgVChWKJNpmLislYg5o%2BkA%3D; unb=3175145922; skt=2151ec73721bfa8c; t=b2bb5c2ce10267ebb8a2340c4a015bec; _cc_=Vq8l%2BKCLiw%3D%3D; tg=5; _l_g_=Ug%3D%3D; _nk_=%5Cu548C%5Cu70ED%5Cu7834%5Cu4EBA%5Cu751F; cookie17=UNGR61JedfNA7Q%3D%3D; uc1=cookie14=UoW%2FVObzcZKgyA%3D%3D&lng=zh_CN&cookie16=W5iHLLyFPlMGbLDwA%2BdvAGZqLg%3D%3D&existShop=false&cookie21=VT5L2FSpdiBh&tag=7&cookie15=UIHiLt3xD8xYTw%3D%3D&pas=0; cna=2tFNEYqY3GACAXL6T0M2HDdL; CNZZDATA30058279=cnzz_eid%3D1822343014-1489425957-https%253A%252F%252Flogin.taobao.com%252F%26ntime%3D1489446478; CNZZDATA1252911424=1607367844-1489424387-%7C1489445988; l=AiMjFDmtjh6WDpe1Hyq3ddmSM23Nk7da; isg=AqOjlmP6_01RCrN4JuiCNWEZMucM6TfajachlNUAc4J5FMM2XWjHKoEGeFPg";

					for (int j = 0; j < jobCountPerThread; j++) {

						Task t;

						try {
							t = new Task("https://2.taobao.com/item.htm?id=544520930215", null, cookie, null);
							ProxyWrapper pw = new ProxyWrapperImpl("127.0.0.1", 8888, "", "");
							t.setProxyWrapper(pw);

							ChromeDriverRequester.getInstance().fetch(t, 60000);

							cookie = t.getResponse().getCookies();

							 if(t.getException() != null){
							 	t.getException().printStackTrace();
							 }

						} catch (MalformedURLException | URISyntaxException e) {
							e.printStackTrace();
						}
						counter.countDown();
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};

			thread.start();
		}

		try {
			counter.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println(System.currentTimeMillis() - t);

		ChromeDriverRequester.getInstance().close();
	}

}