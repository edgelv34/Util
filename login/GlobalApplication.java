public class GlobalApplication extends Application {

    private static volatile GlobalApplication GLOBAL_INSTANCE = null;
	
    @Override
    public void onCreate() {
        super.onCreate();
        GLOBAL_INSTANCE = this;
        try {
            KakaoSDK.init(new KakaoSDKAdapter());
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    public static GlobalApplication getGlobalApplicationContext() {
        if(GLOBAL_INSTANCE == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return GLOBAL_INSTANCE;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        GLOBAL_INSTANCE = null;
    }
}
