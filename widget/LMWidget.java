public class LMWidget extends AppWidgetProvider {
 
    /**
     * Manifest 에 등록한 LMWidget 의 Action 들을 받아서 처리
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        //=============== Boot Complete ===============
        if (Const.BOOT_COMPLETE.equals(action)) {
            Logger.d("BOOT COMPLETE");
            int curBuildVersion = Build.VERSION.SDK_INT;
            if (curBuildVersion >= Build.VERSION_CODES.M && curBuildVersion < Build.VERSION_CODES.N) {
                //만약에 N 또는 O 버전이하의 안드로이드에서 JobService 가 아닌 AlarmManager를 사용할 경우 M 버전이상이면 Doze 모드로 인해 다시 구동시켜줘야함
            }
        //=============== APPWidget Update ===============
        } else if(AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if(extras != null)
            {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if(appWidgetIds != null && appWidgetIds.length > 0) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        } else if (Const.WIDGET_BROADCAST_CHECK) {
			Logger.d("WIDGET_BROADCAST_CHECK OK");
		}

    }

    /**
     * 모든 UI Update
     * @param context
     */
    private void goUpdate(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        this.onUpdate(context, manager, manager.getAppWidgetIds(new ComponentName(context, LMWidget.class)));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews views = setRemoteViews(context);
        final int N = appWidgetIds.length;
        for (int i = 0 ; i < N ; i++) {
            Logger.d("widget onUpdate");
            int appWidgetId = appWidgetIds[i];
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /**
     * 위젯에 멀티 버튼 추가하기
     * 모든 UI Set (update 할 ui)
     */
    private RemoteViews setRemoteViews(Context context)
    {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lm_widget);

        views.setOnClickPendingIntent(R.id.widget_app, setPendingIntent(context, Const.WIDGET_BTN_TYPE_APP));
        views.setOnClickPendingIntent(R.id.widget_refresh_btn, setPendingSelf(context, Const.WIDGET_BTN_TYPE_REFRESH));

        return views;
    }

    /**
     * Refresh Button 을 눌렀을 때 Event
     * @param context
     * @param type
     * @return
     */
    private PendingIntent setPendingSelf(Context context, int type) {
        Logger.d("setPendingSelf");
        Intent intent = new Intent(context, LMWidget.class);
        intent.putExtra("btnclick", true);
        intent.setAction(Const.WIDGET_BROADCAST_CHECK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, type, intent, 0);
        return pendingIntent;
    }

    /**
     * app 버튼을 눌렀을 때 Event (위젯에서 app 버튼을 누르면 app 이 켜짐)
     * @param context
     * @param type
     * @return
     */
    private PendingIntent setPendingIntent(Context context, int type)
    {
        Logger.d("setPendingIntent type : " + type);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, SplashActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pi = stackBuilder.getPendingIntent(type, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

}
