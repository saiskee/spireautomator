package spireautomator;

/**
 * Executable functionality tested on:
 * Chrome on Windows
 * Chrome on Mac
 * Chrome on Linux
 * Firefox on Mac
 * Firefox on Linux
 *
 * Selenium functionality tested on:
 * Chrome on Windows
 * Chrome on Linux
 *
 * Known Selenium functionality problems on:
 * Firefox on Linux
 */
public enum OSBrowser {
    WIN_CHROME("Google Chrome for Windows", "https://chromedriver.storage.googleapis.com/2.30/chromedriver_win32.zip"),
    MAC_CHROME("Google Chrome for Mac", "https://chromedriver.storage.googleapis.com/2.30/chromedriver_mac64.zip"),
    NIX_CHROME("Google Chrome for Linux", "https://chromedriver.storage.googleapis.com/2.30/chromedriver_linux64.zip"),
    WIN_FIREFOX("Mozilla Firefox for Windows", "https://github.com/mozilla/geckodriver/releases/download/v0.17.0/geckodriver-v0.17.0-win32.zip"),
    MAC_FIREFOX("Mozilla Firefox for macOS", "https://github.com/mozilla/geckodriver/releases/download/v0.17.0/geckodriver-v0.17.0-macos.tar.gz"),
    NIX_FIREFOX("Mozilla Firefox for Linux", "https://github.com/mozilla/geckodriver/releases/download/v0.17.0/geckodriver-v0.17.0-linux64.tar.gz"),
    WIN_IE("Internet Explorer", "http://selenium-release.storage.googleapis.com/3.4/IEDriverServer_Win32_3.4.0.zip"),
    WIN_EDGE("Microsoft Edge", "https://download.microsoft.com/download/3/4/2/342316D7-EBE0-4F10-ABA2-AE8E0CDF36DD/MicrosoftWebDriver.exe"),
    MAC_SAFARI("", "");

    private final String url;
    private final String name;

    OSBrowser(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static OSBrowser getOsBrowser(SpireAutomator.OS os, SpireAutomator.Browser browser) {
        OSBrowser result = null;
        switch(os) {
            case WIN:   switch(browser){
                case CHROME:    result = WIN_CHROME;    break;
                case FIREFOX:   result = WIN_FIREFOX;   break;
                case IE:        result = WIN_IE;        break;
                case EDGE:      result = WIN_EDGE;      break;
                case SAFARI:    result = null;          break;
            }   break;
            case MAC:   switch(browser){
                case CHROME:    result = MAC_CHROME;    break;
                case FIREFOX:   result = MAC_FIREFOX;   break;
                case IE:        result = null;          break;
                case EDGE:      result = null;          break;
                case SAFARI:    result = MAC_SAFARI;    break;
            }   break;
            case NIX:   switch(browser){
                case CHROME:    result = NIX_CHROME;    break;
                case FIREFOX:   result = NIX_FIREFOX;   break;
                case IE:        result = null;          break;
                case EDGE:      result = null;          break;
                case SAFARI:    result = null;          break;
            }   break;
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
