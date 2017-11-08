# SNS Login
#### 일반적인 로그인 로직에 SNS (KAKAO, NAVER, FACEBOOK, GOOGLE) 로그인 정보를 확인할 수 있다


* res 폴더에 있는 것들은 포함하지않았음
* java 파일들 간략설명
  * SignInSns.java : SNS Login Info 를 얻을 수 있다.
  * GlobalApplication.java , KakaoSDKAdapter.java , KakaoSignupActivity.java : kakao login 에 사용된다.
  * LoginActivity.java : 로그인 로직이 있다.


<pre>
<code>
kakao 와 같은 경우 Application 동작 시 init 과정을 거치게 된다.
그리고 kakao 는 시작메소드가 따로 존재하지않으므로 button (com.kakao.usermgmt.LoginButton) 을 눌렀을 때 로그인 정보를 가져오기 시작한다.
<hr/>
[이 외의 sns 시작 사용법]

naver : 
mSignInSns.startLoginNaver();

facebook : 
mSignInSns.facebookLogin();

google : 
mSignInSns.googleSNSLoginStart();
</code>
</pre>
