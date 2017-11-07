public class SignInSns {

    private Context mContext;

    //google
    private GoogleApiClient mGoogleApiClient;
    public final static int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;

    //naver
    public static OAuthLogin mOAuthLoginInstance;
    public Map<String,String> mUserInfoMap;

    //kakao
    private SessionCallback mKakaoCallBack;
    public final static int REQUEST_CODE_KAKAO = 0x1;
    public final static int REQUEST_CODE_KAKAO_SIGN = 0x2;

    //face
    public final static int REQUEST_CODE_FACE = 0xFACE;
    private CallbackManager mFaceCallbackManager;
    private Button mFaceBookLoginBtn;

    private String loginType = "";

    public SignInSns(Context context) {
        this.mContext = context;
       
        //kakao
        kakaoSNSLoginInit();
    }

    //------------------------------------------------------- google -------------------------------------------------------//


    public void googleSNSLoginInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(((FragmentActivity)mContext), failedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    GoogleApiClient.OnConnectionFailedListener failedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };


    public void googleSNSLoginStart() {
		googleSNSLoginInit();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Logger.d("Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Logger.d("handleSignInResult:" + result.isSuccess());
        hideProgressDialog();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            // mStatusTextView.setText(acct.getDisplayName());
            Logger.d(acct.getDisplayName() + " " + acct.getEmail() + " " + acct.getIdToken() + "  " + acct.getServerAuthCode() + "  " + acct.getId());

//                Intent intent = new Intent(mContext, MainActivity.class);
			loginType = "google";

//                ((Activity)mContext).setResult(RESULT_OK, intent);
//                ((Activity)mContext).finish();

			HashMap<String, String> map = new HashMap<>();

			map.put("id", acct.getId());
			map.put("name", acct.getDisplayName());
			map.put("email", acct.getEmail());
			map.put("forwardUrl", mForwardUrl);
			map.put("loginType", loginType);

			AppConfig.gotoFinish(MainActivity.LOGIN_CONNECT, map, mContext);

        } else {
            // Signed out, show unauthenticated UI.
            signIn();
        }
    }

    /**
     * log-in
     */
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((Activity)mContext).startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * log-out
     */
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    /**
     * disconnected
     */
    public void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    //------------------------------------------------------- google -------------------------------------------------------//
    //------------------------------------------------------- naver -------------------------------------------------------//

    public void naverSNSLoginInit() {
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.init(mContext, mContext.getString(R.string.naver_app_key), mContext.getString(R.string.naver_secret), AppConfig.APP_NAME);
    }

    public void startLoginNaver() {
		naverSNSLoginInit();
        mOAuthLoginInstance.startOauthLoginActivity((Activity)mContext, mOAuthLoginHandler);
    }

    public OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);
                Logger.d("accessToken : " + accessToken + " , refreshToken : " + refreshToken + " , expiresAt : " + String.valueOf(expiresAt) + " , tokenType : " + tokenType + " , mOAuthLoginInstance.getState : " + mOAuthLoginInstance.getState(mContext).toString());
                new RequestApiTask().execute();
            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Logger.d("login fail : errorCode : " + errorCode + " , errorDesc : " + errorDesc);
            }
        }
    };

    public void naverLogOut() {
//        mOAuthLoginInstance.logout(LoginActivity.this); //only logout not delete token
        NaverLogOutTask logOutTask = new NaverLogOutTask();
        logOutTask.execute();
    }

    public class NaverLogOutTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Logger.d("errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
                Logger.d("errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
            } else {
                Logger.d("success");
            }
            return null;
        }
    }

    public class RequestApiTask extends AsyncTask<Void, Void, String> {
		
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return getNaverUserInfo(at);
        }

        protected void onPostExecute(String userInfoJson) {
            if (userInfoJson != null) {
                Logger.d("userInfoJson : " + userInfoJson);
                naverUserjsonParsing(userInfoJson);
            } else {
                Toast.makeText(mContext, "로그인 실패하였습니다.  잠시후 다시 시도해 주세요!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getNaverUserInfo(String accessToken) {
        boolean isError = false;
        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가 , 네이버 로그인 접근 토큰
        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);
            con.setRequestProperty("Content-Type", "application/xml");
            int responseCode = con.getResponseCode();
            Logger.d("Content-type : " + con.getContentType());
            BufferedReader br;
            if(responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                isError = true;
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            if (isError) {
                return  null;
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void naverUserjsonParsing(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            String resultCode = jsonObject.getString("resultcode");
            String message = jsonObject.getString("message");
            if (message.equals("success")) {
                String response = jsonObject.getString("response");
                JSONObject responseJson = new JSONObject(response);
                String nickName = responseJson.getString("nickname");
                String enc_id = responseJson.getString("enc_id");
                String profile_image = responseJson.getString("profile_image");
                String age = responseJson.getString("age");
                String gender = responseJson.getString("gender");
                String id = responseJson.getString("id");
                String name = responseJson.getString("name");
                String email = responseJson.getString("email");
                String birthday = responseJson.getString("birthday");
                Logger.d("nickName : " + nickName + " , name : " + name + " , email : " + email + " , id : " + id + " , birthday : " + birthday + " , enc_id : " + enc_id);

				loginType = "naver";
				HashMap<String, String> map = new HashMap<>();

				map.put("enc_id", enc_id);
				map.put("name", name);
				map.put("nickname", nickName);
				map.put("email", email);
				map.put("gender", gender);
				map.put("age", age);
				map.put("birthday", birthday);
				map.put("forwardUrl", mForwardUrl);
				map.put("loginType", loginType);

				AppConfig.gotoFinish(MainActivity.LOGIN_CONNECT, map,mContext);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    //------------------------------------------------------- naver -------------------------------------------------------//
    //------------------------------------------------------- kakao -------------------------------------------------------//

    public void kakaoSNSLoginInit() {
        mKakaoCallBack = new SessionCallback();
        Session.getCurrentSession().addCallback(mKakaoCallBack);
    }

    public class SessionCallback implements ISessionCallback {

        /**
         * 세션 연결 성공
         */
        @Override
        public void onSessionOpened() {
            Logger.d("KAKAO Session Open");
            redirectSignupActivity();
        }

        /**
         * 세션 연결 실패
         * @param exception 발생한 문제
         */
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Logger.d("KAKAO Session Open Failed");
            if(exception != null) {
                Logger.e("KakaoException : " + exception);
            }
//            setContentView(R.layout.activity_login);    //세션 연결에 실패하면 다시 로그인 화면을 불러옴
        }
    }

    public void removeKaKaOSession() {
        Session.getCurrentSession().removeCallback(mKakaoCallBack);
    }

    protected void redirectSignupActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(mContext, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ((Activity)mContext).startActivityForResult(intent, REQUEST_CODE_KAKAO_SIGN);
    }

    public void kakaoLogOut() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //로그아웃 성공 후 하고싶은 내용 코딩 ~
                Logger.d("KaKaO LogOut");
            }
        });
    }


    //------------------------------------------------------- kakao -------------------------------------------------------//
    //------------------------------------------------------- facebook -------------------------------------------------------//

    public CallbackManager facebookSNSLoginInit() {
        return mFaceCallbackManager = CallbackManager.Factory.create();
    }

    public void faceResult(int requestCode, int resultCode, Intent data) {
        mFaceCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * FaceBook Login 을 시도 하고 결과를 받는 메소드
     */
    public void facebookLogin() {
		facebookSNSLoginInit();
        LoginManager.getInstance().logInWithReadPermissions((Activity)mContext, Arrays.asList("public_profile","email"));
        LoginManager.getInstance().registerCallback(mFaceCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Logger.d("페이스북 토큰 -> " + loginResult.getAccessToken().getToken());
                Logger.d("페이스북 UserID -> " + loginResult.getAccessToken().getUserId());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Logger.d("페이스북 로그인 결과" + response.toString());

                                if (response.getError() == null) {
                                    try {
                                        String id = object.getString("id");
                                        String email = object.getString("email");       // 이메일
                                        String name = object.getString("name");         // 이름
                                        String gender = object.getString("gender");     // 성별

                                        Logger.d("facebook id : " + id + " , facebook email : " + email + " , facebook name : " + name + " , facebook gender : " + gender);

                                        if (isMain) {
                                            ((MainActivity)mContext).snsLoginFacebook(id, name, email, "");
                                        } else {
//                                            Intent intent = new Intent(mContext, MainActivity.class);
                                            loginType = "facebook";

//                                            ((Activity)mContext).setResult(RESULT_OK, intent);
//                                            ((Activity)mContext).finish();

                                            HashMap<String, String> map = new HashMap<>();

                                            map.put("id", id);
                                            map.put("name", name);
                                            map.put("email", email);
                                            map.put("gender", gender);
                                            map.put("forwardUrl", mForwardUrl);
                                            map.put("loginType", loginType);

                                            AppConfig.gotoFinish(MainActivity.LOGIN_CONNECT, map,mContext);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //error 일 경우
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Logger.d("facebook login error : " + error);
            }
        });
    }


    /**
     * FaceBook에 부여된 권한을 취소시킴
     */
    public void deleteFacebookApplication(){
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                boolean isSuccess = false;
                if (response == null) {
                    return;
                }
                try {
                    Logger.d("response.getJSONObject() : " + response.getJSONObject());
                    if (response.getJSONObject() != null) {
                        isSuccess = response.getJSONObject().getBoolean("success");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess && response.getError()==null){
                    // Application deleted from Facebook account
                }

            }
        }).executeAsync();
    }

    //------------------------------------------------------- facebook -------------------------------------------------------//


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void endmGoogleApiClient() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) mContext);
            mGoogleApiClient.disconnect();
        }
    }

    public void getSNSInfoEnd() {
        hideProgressDialog();
        removeKaKaOSession();
        endmGoogleApiClient();
    }

}
