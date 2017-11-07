public class LoginActivity extends AppCompatActivity{

    ImageView loginClose;
    AutoCompleteTextView login_id_et;
    EditText login_pw_et;
    Button loginBtn;
    CheckBox cbPersistId, cbAutoLogin;
    Button joinBtn;
    Button loginNaver, loginFacebook;
    LoginButton loginKakao;
    SignInButton loginGoogle;
    Button logoutGoolge;
    Button logoutNaver;
    Button logoutKakao;
    Button logoutFaceBook;
	
    boolean persistId_enabled = false;
    boolean autoLogin_enabled = false;
	
    SharedPreferences setting;
    SharedPreferences.Editor editor;
	
    String loginType;
    Button lost_id_pw;

    private final int REQUEST_CODE_MESSAGE = 0x4;
    private final int REQUEST_CODE_SETTING = 0x5;

    private final static String LOGIN_TYPE_NORMAL = "normal";
    private final static String LOGIN_TYPE_GOOGLE = "google";
	
    private SignInSns mSignInSns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logins_activity);

        Logger.d("LoginActivity onCreate");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//adjust pan 을 함으로써 들어오자마자 키보드가 뜨는 것을 방지한다.

        setting = getSharedPreferences(SharedPreferenceDefine.PREF_TYPE_SETTING, 0);	//persistId_enabled : id 를 저장할 것 인지 아닌지 pref 에 저장하고 가져오도록 함 , autoLogin_enabled : 자동 로그인을 허용할 것인지 아닌지 pref 에 저장하고 가져오도록 함
        editor= setting.edit();

        persistId_enabled = setting.getBoolean(SharedPreferenceDefine.PREF_TYPE_SETTING_PERSIST_ID, false);
        autoLogin_enabled = setting.getBoolean(SharedPreferenceDefine.PREF_TYPE_SETTING_AUTO_LOGIN, false);

        RelativeLayout loginLayout = (RelativeLayout) findViewById(R.id.login_layout);

        loginClose = (ImageView) findViewById(R.id.login_close);

        login_id_et = (AutoCompleteTextView) findViewById(R.id.login_id_et);
        login_pw_et= (EditText) findViewById(R.id.login_pw_et);
        loginBtn = (Button) findViewById(R.id.login_btn);

        joinBtn = (Button) findViewById(R.id.join_btn);

		//======================================== SNS ========================================
        loginNaver = (Button) findViewById(R.id.login_naver);
        logoutNaver = (Button) findViewById(R.id.logout_naver);
		
        loginKakao = (LoginButton) findViewById(R.id.login_kakao);
        logoutKakao = (Button) findViewById(R.id.logout_kakao);
		
        loginFacebook = (Button) findViewById(R.id.login_facebook);
        logoutFaceBook = (Button) findViewById(R.id.logout_facebook);
		
        loginGoogle = (SignInButton) findViewById(R.id.login_google);
        logoutGoolge = (Button) findViewById(R.id.logout_goolge);

		//======================================== SNS ========================================
		
        cbPersistId = (CheckBox) findViewById(R.id.id_save_chk);
        cbAutoLogin = (CheckBox) findViewById(R.id.auto_login_chk);
        cbPersistId.setChecked(persistId_enabled);
        cbAutoLogin.setChecked(autoLogin_enabled);

        lost_id_pw = (Button) findViewById(R.id.lost_id_pw_btn);


        loginClose.setOnClickListener(onSingleClickListener);
        lost_id_pw.setOnClickListener(onSingleClickListener);
        joinBtn.setOnClickListener(onSingleClickListener);
        loginBtn.setOnClickListener(onSingleClickListener);
        loginNaver.setOnClickListener(onSingleClickListener);
        logoutNaver.setOnClickListener(onSingleClickListener);
        loginKakao.setOnClickListener(onSingleClickListener);
        logoutKakao.setOnClickListener(onSingleClickListener);
        loginFacebook.setOnClickListener(onSingleClickListener);
        logoutFaceBook.setOnClickListener(onSingleClickListener);
        loginGoogle.setOnClickListener(onSingleClickListener);
        logoutGoolge.setOnClickListener(onSingleClickListener);

        mSignInSns = new SignInSns(this, false);

        if(persistId_enabled) {
            login_id_et.setText(setting.getString(SharedPreferenceDefine.PREF_TYPE_SETTING_USER_ID, ""));//User
        }

		// 만약에 아이디 저장을 체크한 상태에서 아이디를 변경할 경우 사용됨
        login_id_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(persistId_enabled){

                    String id = s.toString();

                    editor.putString(SharedPreferenceDefine.PREF_TYPE_SETTING_USER_ID, id);
                    editor.putBoolean(SharedPreferenceDefine.PREF_TYPE_SETTING_PERSIST_ID, true);
                    editor.commit();

                }

            }

        });

        login_pw_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER){

                    nomalLogin();

                }

                return false;

            }
        });

        cbPersistId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    String id = login_id_et.getText().toString();

                    persistId_enabled = true;
                    editor.putString(SharedPreferenceDefine.PREF_TYPE_SETTING_USER_ID, id);
                    editor.putBoolean(SharedPreferenceDefine.PREF_TYPE_SETTING_PERSIST_ID, true);
                    editor.commit();

                } else {

                    persistId_enabled = false;
                    editor.remove(SharedPreferenceDefine.PREF_TYPE_SETTING_USER_ID);
                    editor.putBoolean(SharedPreferenceDefine.PREF_TYPE_SETTING_PERSIST_ID, false);
                    editor.commit();

                }

            }
        });

        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                editor.putBoolean(SharedPreferenceDefine.PREF_TYPE_SETTING_AUTO_LOGIN, isChecked).commit();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
	
	/**
	* 일반 로그인 (id , pw 를 쳐서 서버에 전달하도록 함)
	*/
	private void nomalLogin() {
		if(login_id_et.getText().toString().equals("") || login_id_et.getText() == null){

			Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();

		} else if (login_pw_et.getText().toString().equals("") || login_pw_et.getText() == null) {

			Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();

		} else {

			String id = login_id_et.getText().toString();
			String pw = login_pw_et.getText().toString();

			LoginActivity.LoginDB task = new LoginActivity.LoginDB();
			task.execute(LOGIN_TYPE_NORMAL, id, pw);
		}						
	}

    OnSingleClickListener onSingleClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.login_close:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;

                case R.id.login_btn:
                    nomalLogin();
                    break;

                case R.id.login_naver:
                    mSignInSns.startLoginNaver();
//                    mOAuthLoginInstance.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
                    break;

                case R.id.logout_naver:
//                    naverLogOut();
                    break;

                case R.id.login_kakao:
                    break;

                case R.id.logout_kakao:
//                    kakaoLogOut();
                    break;

                case R.id.login_facebook:
//                    if (AccessToken.getCurrentAccessToken() != null) {
//                        Logger.d("허용된 권한 : " + AccessToken.getCurrentAccessToken().getPermissions());
//                        LoginManager.getInstance().logOut();
//                        Logger.d("facebook LogOut");
//                        return;
//                    } else {
                    mSignInSns.facebookLogin();
//                    }

                    break;

                case R.id.logout_facebook:
//                    deleteFacebookApplication();
                    break;

                case R.id.login_google:
                    mSignInSns.googleSNSLoginStart();
                    break;

                case R.id.logout_goolge:
//                    signOut();
                    break;

                case R.id.lost_id_pw_btn:
                    HashMap<String, String> lostIDHashMap = new HashMap<>();
                    lostIDHashMap.put("loginType", "lost_id_pw");
                    AppConfig.gotoFinish(MainActivity.LOGIN_CONNECT, lostIDHashMap,LoginActivity.this);
                    break;

            }
        }
    };

    /**
     * Login 버튼을 눌렀을 때 로그인 정보가 맞는 지 HttpUrlConnection을 통해 확인
     * Http 통신을 하기때문에 AsyncTask 사용
     * 서버에 id 와 pw를 전달 후 일치하여 success가 되면 MainActivity에 id, pw를 넘겨주면서 웹뷰에서 로그인 할 수있도록 함
	 * pw 는 인코딩 및 디코딩 과정을 거쳐서 작업해줘야함 (해당 문서는 샘플로 해당 과정은 존재하지않음)
     */
    class LoginDB extends AsyncTask<String, Void, String> {
        String param0;
        String param1;
        String param2;
        String param3;
        String link;
        String data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("success")) {
                isWebClear = false;
                loginType = "normal";
                HashMap<String , String> map = new HashMap<String , String>();

                map.put("id", param1);
                map.put("pw", param2);
                map.put("loginType", loginType);
				
                AppConfig.gotoFinish(MainActivity.LOGIN_CONNECT, map,LoginActivity.this);
				
                Toast.makeText(LoginActivity.this, getString(R.string.success_login), Toast.LENGTH_SHORT).show();

            } else if (s.equals("fail")) {
                Toast.makeText(LoginActivity.this, getString(R.string.id_pw_fail), Toast.LENGTH_SHORT).show();
            } else {
				Logger.d(s);
			}
        }

        @Override
        protected String doInBackground(String... params) {

            Logger.d("login check task");

            try{
                //Param length 확인한 후 Param 배열 추가해야함
                param0 = (String)params[0];//type
                param1 = (String)params[1];//id
                param2 = (String)params[2];//pw

                link = AppConfig.WEB_LOGIN_CHECK; //url for login id, pw check
                data = URLEncoder.encode("mb_id", "UTF-8") + "=" + URLEncoder.encode(param1, "UTF-8");
                data += "&" + URLEncoder.encode("mb_password", "UTF-8") + "=" + URLEncoder.encode(param2, "UTF-8");

                URL url = new URL(link);

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();

                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });

                HttpURLConnection conn = httpsURLConnection;
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            }
            catch(Exception e){
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());
            }

        }
    }

    private final LoginHandler mHandler = new LoginHandler(this);

    private static class LoginHandler extends Handler {
        private final WeakReference<LoginActivity> loginActivityWeakReference;
        public LoginHandler(LoginActivity loginActivity) {
            loginActivityWeakReference = new WeakReference<LoginActivity>(loginActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity loginActivity = loginActivityWeakReference.get();
            if (loginActivity != null) {
                loginActivity.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        Logger.d("requestCode : " + requestCode + " , resultCode : " + resultCode + " , data : " + data);
        switch (requestCode) {
            case SignInSns.RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    mSignInSns.handleSignInResult(result);
                }
                break;

            case SignInSns.REQUEST_CODE_KAKAO:
                if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                    return;
                }
                break;

            case SignInSns.REQUEST_CODE_KAKAO_SIGN:
                if (resultCode == RESULT_OK) {
                    String id = data.getStringExtra("id");
                    String name = data.getStringExtra("name");
                    String nickName = data.getStringExtra("nickName");
                    String email = data.getStringExtra("email");

                    loginType = "kakao";
                    HashMap<String , String> map = new HashMap<String , String>();

                    map.put("id", id);
                    map.put("name", name);
                    map.put("nickname", nickName);
                    map.put("email", email);
                    map.put("forwardUrl", mForwardUrl);
                    map.put("loginType", loginType);

                    AppConfig.gotoFinish(MainActivity.LOGIN_CONNECT, map,LoginActivity.this);
                }
                break;

            case SignInSns.REQUEST_CODE_FACE:
                mSignInSns.faceResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSignInSns.getSNSInfoEnd();
    }
}
