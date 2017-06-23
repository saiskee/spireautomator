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
public enum WebDriverExecutable {
    CHROME_WIN32 ("chromedriver-win32.exe", "https://chromedriver.storage.googleapis.com/2.30/chromedriver_win32.zip"),
    CHROME_MAC64 ("chromedriver-mac64", "https://chromedriver.storage.googleapis.com/2.30/chromedriver_mac64.zip"),
    CHROME_LNX64 ("chromedriver-linux64", "https://chromedriver.storage.googleapis.com/2.30/chromedriver_linux64.zip"),
    FIREFOX_WIN64 ("geckodriver-win32.exe", "https://github.com/mozilla/geckodriver/releases/download/v0.17.0/geckodriver-v0.17.0-win32.zip"),
    FIREFOX_MACOS ("geckodriver-macos", "https://github.com/mozilla/geckodriver/releases/download/v0.17.0/geckodriver-v0.17.0-macos.tar.gz"),
    FIREFOX_LNX64 ("geckodriver-linux64", "https://github.com/mozilla/geckodriver/releases/download/v0.17.0/geckodriver-v0.17.0-linux64.tar.gz");

    private final String url;
    private final String fileName;

    WebDriverExecutable(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }
}
