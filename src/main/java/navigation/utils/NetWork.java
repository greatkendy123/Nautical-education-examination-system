package navigation.utils;

import java.lang.reflect.Method;

public class NetWork {
    /**
     * 调用系统浏览器打开url
     *
     * @param url 请求打开的url
     */
    public static void openLink(String url) throws Exception {

        String osName = System.getProperty("os.name");

        if (osName.startsWith("Mac OS")) {

            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
            openURL.invoke(null, new Object[]{url});

        } else if (osName.startsWith("Windows")) {

            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);

        } else {

            String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)

                if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0)
                    browser = browsers[count];
            if (browser == null)
                throw new Exception("没有找到合适的浏览器");
            else
                Runtime.getRuntime().exec(new String[]{browser, url});
        }
    }
}
