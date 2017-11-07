# Util Project
### 각 기능들을 사용할 수 있는 Util 형 소스들을 모아 놓은 Project
1. Foreground, Background 판별
* GlobalApplication.java 이용
* 사용법
<pre>
<code>
if (getApplication() instanceof GlobalApplication) {
    if (((GlobalApplication)getApplication()).isForeground()) {
        Logger.d("foregound");
    } else {
        Logger.d("backgound");
    }
} else {
    Logger.d("Application Name : " + getApplication() + " , packageName : " + getApplication().getPackageName());
}
</code>
</pre>