public class KakaoSignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    protected void requestMe() {
        Logger.d("");
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Logger.d("Session is Closed");
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                Logger.d("Not Signed User");
            }

            //성공 시 userProfile 형태로 반환
            @Override
            public void onSuccess(UserProfile result) {
                Logger.d("Success!!");
                redirectLoginActivity(result); // 로그인 성공시 LoginActivity
            }
        });
    }


    private void redirectMainActivity() {
//        startActivity(new Intent(this, MainActivity.class));
//        finish();
    }

    protected void redirectLoginActivity() {
        redirectLoginActivity(null);
    }

    protected void redirectLoginActivity(UserProfile result) {

        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        if (result != null) {

            String id = String.valueOf(result.getId()); // userProfile에서 ID값을 가져옴
            String nickname = result.getNickname();     // Nickname 값을 가져옴
            String name = nickname;
            String email = result.getEmail();
            Logger.d("UserProfile : " + result + " , kakaoID : " + id + " , kakaoNickname : "+ nickname + " , kakaoEmail : " + email);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("nickName", nickname);
            intent.putExtra("email", email);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

}
